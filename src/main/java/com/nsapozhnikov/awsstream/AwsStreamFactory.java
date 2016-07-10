package com.nsapozhnikov.awsstream;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class AwsStreamFactory {
    public static <AWSREQ, AWSRESP, R> Stream<R> createStream(Function<AWSREQ, AWSRESP> queryFunc,
            AWSREQ requestParameter, Function<AWSRESP, Iterator<R>> func) {
        return StreamSupport.stream(new AwsIterable<>(queryFunc, requestParameter, func).spliterator(), false);
    }
}
