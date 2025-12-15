package com.deuceng.moviedive.handler;

import lombok.Getter;
import lombok.Setter;


import java.util.Date;

@Getter
@Setter
public class Exception <E>
{
    private String hostName;
    private String path;
    private Date ceateTime;
    //private ZonedDateTime createTime;
    private E message;
}
