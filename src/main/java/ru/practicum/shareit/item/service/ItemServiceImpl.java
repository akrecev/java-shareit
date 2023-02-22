package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.BadRequestException;
import ru.practicum.shareit.exception.model.DataNotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.MyPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBookingDto;
import static ru.practicum.shareit.item.CommentMapper.toComment;
import static ru.practicum.shareit.item.CommentMapper.toCommentDtoResponse;
import static ru.practicum.shareit.item.ItemMapper.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository requestRepository;


    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new DataNotFoundException("Request Id=" + itemDto.getRequestId()));
        }
        Item savedItem = itemRepository.save(toItem(itemDto, owner, request));

        return toItemDto(savedItem);
    }

    @Override
    @Transactional
    public CommentDtoResponse commentCreate(Long userId, Long itemId, CommentDto commentDto) {

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item Id=" + itemId));

        bookingRepository.findBookingPast(userId, LocalDateTime.now(),
                        new MyPageRequest(0, 20, Sort.unsorted()))
                .stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("User id=" + userId + " did not use item id=" + itemId));
        Comment comment = toComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        return toCommentDtoResponse(savedComment);
    }

    @Override
    public ItemDtoResponse get(Long userId, Long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item id=" + itemId));

        ItemDtoResponse responseItem = toItemDtoResponse(item);
        if (userId.equals(item.getOwner().getId())) {
            responseItem.setLastBooking(
                    toBookingDto(
                            bookingRepository.findLast(itemId, LocalDateTime.now(), PageRequest.of(0, 1))
                    )
            );
            responseItem.setNextBooking(
                    toBookingDto(
                            bookingRepository.findNext(itemId, LocalDateTime.now(), PageRequest.of(0, 1))
                    )
            );
        }
        List<CommentDtoResponse> commentList = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDtoResponse)
                .collect(Collectors.toList());
        responseItem.setComments(commentList);

        return responseItem;
    }

    @Override
    public List<ItemDtoResponse> getUserItems(Long userId, Integer from, Integer size) {

        return itemRepository
                .findAllByOwnerIdOrderById(userId, new MyPageRequest(from, size, Sort.unsorted()))
                .stream()
                .map(ItemMapper::toItemDtoResponse)
                .peek(itemDtoResponse -> itemDtoResponse.setLastBooking(
                        toBookingDto(
                                bookingRepository.findLast(itemDtoResponse.getId(),
                                        LocalDateTime.now(), PageRequest.of(0, 1))
                        )
                ))
                .peek(itemDtoResponse -> itemDtoResponse.setNextBooking(
                        toBookingDto(
                                bookingRepository.findNext(itemDtoResponse.getId(),
                                        LocalDateTime.now(), PageRequest.of(0, 1))
                        )
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getSearchItems(String searchText, Integer from, Integer size) {
        List<ItemDto> result = new ArrayList<>();
        if (!searchText.isBlank()) {
            result = itemRepository.search(searchText, new MyPageRequest(from, size, Sort.unsorted()))
                    .stream()
                    .filter(Item::getAvailable)
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }

        return result;
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {

        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User Id=" + userId));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item Id=" + itemId));

        throwNotOwnerRequest(userId, item);

        if (itemDto.getName() != null && !itemDto.getName().equals(item.getName())) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null && !itemDto.getDescription().equals(item.getDescription())) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null && !itemDto.getAvailable().equals(item.getAvailable())) {
            item.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(item);

        return toItemDto(updatedItem);
    }

    @Override
    @Transactional
    public void delete(Long userId, Long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Item Id=" + itemId));

        throwNotOwnerRequest(userId, item);

        itemRepository.deleteById(itemId);
    }

    private void throwNotOwnerRequest(Long userId, Item item) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("User id=" + userId + " is not owner of item " + item);
        }
    }
}
