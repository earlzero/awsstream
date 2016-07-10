package com.nsapozhnikov.awsstream;

import java.util.Iterator;
import java.util.function.Function;

public class AwsIterable<AWSREQ, AWSRESP, R> implements Iterable<R> {

    AwsIterator<AWSREQ, AWSRESP, R> iterator;

    public AwsIterable(Function<AWSREQ, AWSRESP> queryFunc, AWSREQ requestParameter,
            Function<AWSRESP, Iterator<R>> func) {

        this.iterator = new AwsIterator<>(queryFunc, func, requestParameter);

    }

    @Override
    public Iterator<R> iterator() {
        return iterator;
    }

}
