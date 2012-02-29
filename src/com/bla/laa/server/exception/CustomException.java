/*
 * @(#)CustomException.java
 *
 * Copyright Swiss Reinsurance Company, Mythenquai 50/60, CH 8022 Zurich. All rights reserved.
 */
package com.bla.laa.server.exception;

public class CustomException extends  Exception{
    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
    }
}

