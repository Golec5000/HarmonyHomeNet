package bwp.hhn.backend.harmonyhomenetlogic.service.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class SmsServiceImp implements SmsService {

    private static final Logger LOG = Logger.getLogger(SmsServiceImp.class.getName());

    @Override
    @Async
    public void sendSms(String text,String recipient) {

        LOG.info("Sending SMS to " + recipient);
        LOG.info("SMS text: " + text);

    }
}