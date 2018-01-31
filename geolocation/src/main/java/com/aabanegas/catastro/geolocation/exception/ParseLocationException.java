package com.aabanegas.catastro.geolocation.exception;

import com.aabanegas.mcs.errorhandling.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ErrorCode(code = "400-0000-0000")
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ParseLocationException extends RuntimeException {

    public ParseLocationException() { super(); }

    public ParseLocationException(String msg) {
        super(msg);
    }

    public ParseLocationException(String msg, Throwable t) {
        super(msg, t);
    }

}
