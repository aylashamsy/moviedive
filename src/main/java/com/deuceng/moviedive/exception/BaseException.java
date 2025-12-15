package com.deuceng.moviedive.exception;


public class BaseException extends RuntimeException {
    public BaseException()
    {
    }

    public BaseException(ErrorMessage errorMessage)
    {
        //Super kullanarak RuntimeException constructor'ına da fırlatıyoruz bu errorMessage'ın metodundan returnlanan değeri(Hata mesajını kaybetmemek için)
        super(errorMessage.prepareErrorMessageWithOfStatic());
    }
}
