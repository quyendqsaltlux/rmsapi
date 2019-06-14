package saltlux.ctv.tranSS.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saltlux.ctv.tranSS.model.Payment;
import saltlux.ctv.tranSS.payload.payment.PaymentReq;
import saltlux.ctv.tranSS.service.PaymentService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;


    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PM') or hasRole('ADMIN') or hasRole('RM')")
    public Payment createCandidate(@Valid @RequestBody PaymentReq paymentReq) throws Exception {
        if (paymentReq == null) {
            throw new MissingServletRequestParameterException(null, null);
        }
        return paymentService.save(paymentReq);
    }

}
