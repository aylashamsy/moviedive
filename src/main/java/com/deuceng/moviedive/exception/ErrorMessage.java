package com.deuceng.moviedive.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage<T>
{
    private MessageType messageType;
    private String ErrorMessage;
    private T ofStatic;


    public String prepareErrorMessageWithOfStatic()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(messageType.getMessage());
        if(ErrorMessage != null)
        {
            builder.append(ErrorMessage);
        }
        if(ofStatic!=null)
        {
            builder.append(" : "+ofStatic);
        }
        return builder.toString();
    }
}
