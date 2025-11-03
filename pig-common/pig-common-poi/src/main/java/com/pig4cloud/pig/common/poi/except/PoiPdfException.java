package com.pig4cloud.pig.common.poi.except;

public class PoiPdfException extends RuntimeException {
    public PoiPdfException(String message) {
        super(message);
    }
    public PoiPdfException(String message, Throwable cause) {
        super(message, cause);
    }
}