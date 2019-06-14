package saltlux.ctv.tranSS.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import saltlux.ctv.tranSS.model.Payment;
import saltlux.ctv.tranSS.payload.payment.PaymentReq;
import saltlux.ctv.tranSS.repository.PaymentRepository;

@Slf4j
@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment save(PaymentReq paymentReq) {
        Long id = null != paymentReq.getId() && paymentReq.getId() > 0 ? paymentReq.getId() : null;
        Payment payment = null == id || id == 0 ? new Payment() : paymentRepository.findById(id).get();
        BeanUtils.copyProperties(paymentReq, payment, "candidateCode");

        return paymentRepository.save(payment);
    }
}