package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = findUser(userId);
        Item savedItem = itemRepository.save(ItemMapper.toItem(itemDto, owner));

        return ItemMapper.toItemDto(savedItem);
    }

    @Override
    public CommentDtoResponse commentCreate(Long userId, Long itemId, CommentDto commentDto) {
        User author = findUser(userId);
        Item item = findItem(itemId);
        bookingRepository.findBookingPast(userId, LocalDateTime.now())
                .stream()
                .filter(booking -> booking.getItem().getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("User id=" + userId + " did not use item id=" + itemId));
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(author);
        comment.setItem(item);

        return CommentMapper.toCommentDtoResponse(commentRepository.save(comment));
    }

    @Override
    public ItemDtoResponse get(Long userId, Long itemId) {
        ItemDtoResponse responseItem = ItemMapper.toItemDtoResponse(findItem(itemId));
        if (userId == findItem(itemId).getOwner().getId()) {
            responseItem.setLastBooking(
                    BookingMapper.toBookingDto(
                            bookingRepository.findLast(itemId, LocalDateTime.now(), PageRequest.of(0, 1))
                    )
            );
            responseItem.setNextBooking(
                    BookingMapper.toBookingDto(
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
    public List<ItemDtoResponse> getUserItems(Long userId) {

        return itemRepository.findAllByOwnerId(userId).stream()
                .map(ItemMapper::toItemDtoResponse)
                .peek(itemDtoResponse -> itemDtoResponse.setLastBooking(
                        BookingMapper.toBookingDto(
                                bookingRepository.findLast(itemDtoResponse.getId(),
                                        LocalDateTime.now(), PageRequest.of(0, 1))
                        )
                ))
                .peek(itemDtoResponse -> itemDtoResponse.setNextBooking(
                        BookingMapper.toBookingDto(
                                bookingRepository.findNext(itemDtoResponse.getId(),
                                        LocalDateTime.now(), PageRequest.of(0, 1))
                        )
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getSearchItems(String searchText) {
        List<ItemDto> result = new ArrayList<>();
        if (!searchText.isBlank()) {
            result = itemRepository.search(searchText)
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
        findUser(userId);
        Item item = findItem(itemId);
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

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long itemId) {
        throwNotOwnerRequest(userId, findItem(itemId));

        itemRepository.deleteById(itemId);
    }

    @Override
    public Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new DataNotFoundException("Item:" + itemId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("User:" + userId));
    }

    private void throwNotOwnerRequest(Long userId, Item item) {
        if (item.getOwner().getId() != userId) {
            throw new DataNotFoundException("User id=" + userId + " is not owner of item " + item);
        }
    }
}
