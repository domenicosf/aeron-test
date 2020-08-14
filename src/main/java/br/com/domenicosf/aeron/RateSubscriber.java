/*
 * Copyright 2014-2020 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.domenicosf.aeron;

import io.aeron.Aeron;
import io.aeron.Subscription;
import io.aeron.driver.MediaDriver;
import io.aeron.samples.RateReporter;
import io.aeron.samples.SampleConfiguration;
import io.aeron.samples.SamplesUtil;
import org.agrona.CloseHelper;
import org.agrona.concurrent.SigInt;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.aeron.samples.SamplesUtil.rateReporterHandler;

/**
 * Example that displays current throughput rate while receiving data.
 */
public class RateSubscriber
{
    private static final int STREAM_ID = io.aeron.samples.SampleConfiguration.STREAM_ID;
    private static final int FRAGMENT_COUNT_LIMIT = io.aeron.samples.SampleConfiguration.FRAGMENT_COUNT_LIMIT;
    private static final boolean EMBEDDED_MEDIA_DRIVER = io.aeron.samples.SampleConfiguration.EMBEDDED_MEDIA_DRIVER;
    private static final String CHANNEL = SampleConfiguration.CHANNEL;

    public static void main(final String[] args) throws Exception
    {
        System.out.println("Subscribing to " + CHANNEL + " on stream id " + STREAM_ID);

        final MediaDriver driver = EMBEDDED_MEDIA_DRIVER ? MediaDriver.launchEmbedded() : null;
        final ExecutorService executor = Executors.newFixedThreadPool(1);
        final Aeron.Context ctx = new Aeron.Context()
            .availableImageHandler(io.aeron.samples.SamplesUtil::printAvailableImage)
            .unavailableImageHandler(io.aeron.samples.SamplesUtil::printUnavailableImage);

        if (EMBEDDED_MEDIA_DRIVER)
        {
            ctx.aeronDirectoryName(driver.aeronDirectoryName());
        }

        final RateReporter reporter = new RateReporter(TimeUnit.SECONDS.toNanos(1), io.aeron.samples.SamplesUtil::printRate);
        final AtomicBoolean running = new AtomicBoolean(true);

        SigInt.register(() ->
        {
            reporter.halt();
            running.set(false);
        });

        try (Aeron aeron = Aeron.connect(ctx);
             Subscription subscription = aeron.addSubscription(CHANNEL, STREAM_ID))
        {
            final Future<?> future = executor.submit(() -> SamplesUtil.subscriberLoop(
                rateReporterHandler(reporter), FRAGMENT_COUNT_LIMIT, running).accept(subscription));

            reporter.run();

            System.out.println("Shutting down...");
            future.get();
        }

        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS))
        {
            System.out.println("Warning: not all tasks completed promptly");
        }

        CloseHelper.quietClose(driver);
    }
}
