package github.darekdan.demospringbootcqrsbank.event;

import github.darekdan.demospringbootcqrsbank.domain.AccountReadModel;
import github.darekdan.demospringbootcqrsbank.repository.AccountReadModelRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.scheduler.Schedulers;

@Service
public class EventProjector {
    private final AccountReadModelRepository readModelRepo;

    public EventProjector(AccountReadModelRepository readModelRepo) {
        this.readModelRepo = readModelRepo;
    }

    @RabbitListener(queues = "account.created.queue")
    public void projectAccountCreated(AccountCreatedEvent event) {
        AccountReadModel model = new AccountReadModel();
        model.setAccountId(event.getAccountId());
        model.setAccountHolder(event.getAccountHolder());
        model.setBalance(event.getInitialBalance());
        model.setStatus("ACTIVE");
        model.setTransactionCount(1L);
        model.setCreatedAt(event.getTimestamp());
        model.setUpdatedAt(event.getTimestamp());

        readModelRepo.save(model)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @RabbitListener(queues = "account.deposited.queue")
    public void projectDeposited(DepositedEvent event) {
        readModelRepo.findByAccountId(event.getAccountId())
                .flatMap(model -> {
                    model.setBalance(event.getNewBalance());
                    model.setTransactionCount(model.getTransactionCount() + 1);
                    model.setUpdatedAt(event.getTimestamp());
                    return readModelRepo.save(model);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @RabbitListener(queues = "account.withdrawn.queue")
    public void projectWithdrawn(WithdrawnEvent event) {
        readModelRepo.findByAccountId(event.getAccountId())
                .flatMap(model -> {
                    model.setBalance(event.getNewBalance());
                    model.setTransactionCount(model.getTransactionCount() + 1);
                    model.setUpdatedAt(event.getTimestamp());
                    return readModelRepo.save(model);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    @RabbitListener(queues = "account.insufficient.funds.queue")
    public void projectInsufficientFunds(InsufficientFundsEvent event) {
        readModelRepo.findByAccountId(event.getAccountId())
                .flatMap(model -> {
                    model.setTransactionCount(model.getTransactionCount() + 1);
                    model.setUpdatedAt(event.getTimestamp());
                    return readModelRepo.save(model);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }
}
