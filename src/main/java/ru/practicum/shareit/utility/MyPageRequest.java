package ru.practicum.shareit.utility;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class MyPageRequest extends PageRequest {

    int from;

    public MyPageRequest(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    @Override
    public long getOffset() {
        return from;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MyPageRequest that = (MyPageRequest) o;
        return from == that.from;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), from);
    }
}
