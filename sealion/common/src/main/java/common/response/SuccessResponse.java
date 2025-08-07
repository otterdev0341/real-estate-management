package common.response;

import common.domain.dto.base.ResListBaseDto;

import java.util.List;



public class SuccessResponse<T> {
    private String message;
    private T data;

    // Constructor for single object or generic type
    public SuccessResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    // Static factory method for a single object
    public static <T> SuccessResponse<T> of(String message, T data) {
        return new SuccessResponse<>(message, data);
    }

    // Static factory method for a list of objects
    public static <T> SuccessResponse<List<T>> ofList(String message, List<T> data) {
        return new SuccessResponse<>(message, data);
    }

    // Static factory method for ResListBaseDto
    public static <T> SuccessResponse<ResListBaseDto<T>> ofResList(String message, ResListBaseDto<T> data) {
        return new SuccessResponse<>(message, data);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

