package com.dev.service.impl;

import com.dev.service.Base62Encoder;
import org.springframework.stereotype.Component;

@Component
public class Base62EncoderImpl implements Base62Encoder {

    private static final char[] BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    @Override
    public String encode(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must be non-negative");
        }

        StringBuilder sb = new StringBuilder();
        do {
            int remainder = (int) (value % 62);
            sb.append(BASE62[remainder]);
            value /= 62;
        } while (value > 0);

        return sb.reverse().toString();
    }
}
