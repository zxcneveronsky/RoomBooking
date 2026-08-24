package com.example.roombooking.mapper;

import com.example.roombooking.dto.request.CreateBookingRequest;
import com.example.roombooking.dto.request.UpdateBookingRequest;
import com.example.roombooking.dto.response.BookingResponse;
import com.example.roombooking.entity.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {
    public BookingEntity toEntity(CreateBookingRequest request) {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setStartAt(request.startAt());
        bookingEntity.setEndAt(request.endAt());
        return bookingEntity;
    }

    public BookingEntity toEntity(UpdateBookingRequest request) {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(request.id());
        bookingEntity.setStartAt(request.startAt());
        bookingEntity.setEndAt(request.endAt());

        return bookingEntity;
    }

    public BookingResponse toResponse(BookingEntity bookingEntity){
        return new BookingResponse(
                bookingEntity.getId(),
                bookingEntity.getRoom().getId(),
                bookingEntity.getRoom().getName(),
                bookingEntity.getStartAt(),
                bookingEntity.getEndAt());
    }
}
