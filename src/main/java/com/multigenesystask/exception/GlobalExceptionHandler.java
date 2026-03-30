package com.multigenesystask.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorDetails> userExceptionHandler(UserException ue, WebRequest req){
		
		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());
		
		return new ResponseEntity<>(err,HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(ProductException.class)
	public ResponseEntity<ErrorDetails> productExceptionHandler(ProductException ue, WebRequest req){
		
		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());
		
		return new ResponseEntity<>(err,HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(CartItemException.class)
	public ResponseEntity<ErrorDetails> cartItemExceptionHandler(CartItemException ue, WebRequest req){
		
		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());
		
		return new ResponseEntity<>(err,HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(OrderException.class)
	public ResponseEntity<ErrorDetails> orderExceptionHandler(OrderException ue, WebRequest req){
		
		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());
		
		return new ResponseEntity<>(err,HttpStatus.BAD_REQUEST);
		
	}
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException me){
		ErrorDetails err=new ErrorDetails(me.getBindingResult().getFieldError().getDefaultMessage(),"validation error",LocalDateTime.now());
		return new ResponseEntity<>(err,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Endpoint not found");

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorDetails> runtimeExceptionHandler(RuntimeException re, WebRequest req) {
	    ErrorDetails err = new ErrorDetails(
	        re.getMessage(),
	        req.getDescription(false),
	        LocalDateTime.now()
	    );
	    return new ResponseEntity<>(err, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> otherExceptionHandler(Exception e, WebRequest req){
		ErrorDetails error=new ErrorDetails(e.getMessage(),req.getDescription(false),LocalDateTime.now());
		
		return new ResponseEntity<>(error,HttpStatus.NOT_ACCEPTABLE);
	}
	
}
