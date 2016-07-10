package com.nsapozhnikov.awsstream;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public class AwsIterator<AWSREQ, AWSRESP, R> implements Iterator<R> {

    private Iterator<R> iter = null;
    private String token;

    private final Function<AWSRESP, Iterator<R>> func;
    private final AWSREQ requestParameter;
    private final Function<AWSREQ, AWSRESP> queryFunc;

    private final MethodHandles.Lookup lookup;
    private MethodHandle nextToken;
    private final MethodHandle setToken;

    public AwsIterator(Function<AWSREQ, AWSRESP> queryFunc, Function<AWSRESP, Iterator<R>> func,
            AWSREQ requestParameter) {
        this.queryFunc = queryFunc;
        this.func = func;
        this.requestParameter = requestParameter;
        lookup = MethodHandles.lookup();

        try {
            setToken = lookup.findVirtual(requestParameter.getClass(), "setNextToken",
                    MethodType.methodType(void.class, new Class<?>[] { String.class }));

        } catch (Throwable e) {
            throw new IllegalArgumentException();
        }
    }

    private void moveNext() {
        Optional<AWSRESP> resp = peek();
        token = resp.flatMap(rsp -> {
            try {

                if (nextToken == null) {
                    nextToken = lookup.findVirtual(rsp.getClass(), "getNextToken", MethodType.methodType(String.class));
                }

                return Optional.ofNullable((String) nextToken.invoke(rsp));
            } catch (Throwable e) {
                return Optional.empty();
            }
        }).orElse(null);
        iter = resp.map(func).orElse(Collections.emptyIterator());
    }

    @Override
    public boolean hasNext() {
        if (iter == null) {
            moveNext();
            return iter.hasNext();
        } else if (iter.hasNext()) {
            return true;
        } else if (token != null) {
            moveNext();
            return iter.hasNext();
        } else {
            return false;
        }
    }

    @Override
    public R next() {
        if (iter.hasNext()) {
            return iter.next();
        } else {
            throw new IllegalStateException();
        }
    }

    private Optional<AWSRESP> peek() {
        if (iter == null || (!iter.hasNext() && token != null)) {
            try {

                setToken.invoke(requestParameter, token);

                return Optional.of(queryFunc.apply(requestParameter));
            } catch (Throwable e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }
}
