package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;
    @Test
    public void itemOrderTest() throws Exception {
    //상품주문 테스트
        //given
        Member member = createMember();

        Book book = createBook("JPA", 10000,10);

        // 주문수량
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문 시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야한다.", 1,getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다. ", 10000,orderCount, getOrder.getTotalPrice());
        assertEquals("주문 수량만큼 재고가 줄어야한다.",8, book.getStockQuantity());

    }

    @Test(expected = NotEnoughStockException.class)
    public void orderOverStockTest() throws Exception {
        // 재고오버주문 테스트
        //given
        Member member = createMember();
        Item item = createBook("JPA",10000,10);

        int orderCount = 11;

        //when
        orderService.order(member.getId(),item.getId(),orderCount);

        //then
        fail("재고 수량 부족 예외가 발생해야 한다.");
    }
    @Test
    public void orderCancleTest() throws Exception {
    //주문취소 테스트
        //given
        Member member = createMember();
        Book item = createBook("JPA", 10000, 10);

        int orderCount = 2;
        //주문
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        //when
        orderService.cancleOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCEL이다.", OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());

    }

    private Book createBook(String name, int price, int stock) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stock);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","마포대로","123-123"));
        em.persist(member);
        return member;
    }
}