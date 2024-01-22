package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    /**
     * 상품등록
    * */
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    /* 변경 감지 기능 사용 */
    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantitiy) {

        // 같은 엔티티를 조회
        Item findItem = itemRepository.findOne(itemId);

        // 데이터 수정
        findItem.setName(name);
        findItem.setPrice(price);
        findItem.setStockQuantity(stockQuantitiy);

    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findOne(Long itemId) {
        return itemRepository.findOne(itemId);
    }
}
