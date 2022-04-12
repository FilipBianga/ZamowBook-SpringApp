package pl.bianga.zamowbook.order.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.bianga.zamowbook.order.application.port.ManipulateOrderUseCase;
import pl.bianga.zamowbook.order.application.port.QueryOrderUseCase;
import pl.bianga.zamowbook.order.domain.Order;
import pl.bianga.zamowbook.order.domain.OrderItem;
import pl.bianga.zamowbook.order.domain.OrderStatus;
import pl.bianga.zamowbook.order.domain.Recipient;
import pl.bianga.zamowbook.web.CreatedURI;
import pl.bianga.zamowbook.commons.Either;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@RequestMapping("/orders")
@RestController
@AllArgsConstructor
public class OrdersController {

    private final ManipulateOrderUseCase manipulateOrder;
    private final QueryOrderUseCase queryOrder;
    @GetMapping
    public List<QueryOrderUseCase.RichOrder> getOrders() {
        return queryOrder.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<QueryOrderUseCase.RichOrder> getOrderById(@PathVariable Long id) {
        return queryOrder.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    @ResponseStatus(CREATED)
    public ResponseEntity<Object> createOrder(@RequestBody CreateOrderCommand command) {
        return manipulateOrder
                .placeOrder(command.toPlaceOrderCommand())
                .handle(
                        orderId -> ResponseEntity.created(orderUri(orderId)).build(),
                        error -> ResponseEntity.badRequest().body(error)
                );
    }
    URI orderUri(Long orderId) {
        return new CreatedURI("/" + orderId).uri();
    }
    @PutMapping("/{id}/status")
    @ResponseStatus(ACCEPTED)
    public void updateOrderStatus(@PathVariable Long id, @RequestBody UpdateStatusCommand command) {
        OrderStatus orderStatus = OrderStatus
                .parseString(command.status)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Unknown status: " + command.status));
        manipulateOrder.updateOrderStatus(id, orderStatus);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        manipulateOrder.deleteOrderById(id);
    }
    @Data
    static class CreateOrderCommand {
        List<OrderItemCommand> items;
        RecipientCommand recipient;
        ManipulateOrderUseCase.PlaceOrderCommand toPlaceOrderCommand() {
            List<OrderItem> orderItems = items
                    .stream()
                    .map(item -> new OrderItem(item.bookId, item.quantity))
                    .collect(Collectors.toList());
            return new ManipulateOrderUseCase.PlaceOrderCommand(orderItems, recipient.toRecipient());
        }
    }
    @Data
    static class OrderItemCommand {
        Long bookId;
        int quantity;
    }
    @Data
    static class RecipientCommand {
        String name;
        String phone;
        String street;
        String city;
        String zipCode;
        String email;
        Recipient toRecipient() {
            return new Recipient(name, phone, street, city, zipCode, email);
        }
    }
    @Data
    static class UpdateStatusCommand {
        String status;
    }

}