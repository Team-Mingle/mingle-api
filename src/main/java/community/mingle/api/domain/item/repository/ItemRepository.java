package community.mingle.api.domain.item.repository;

import community.mingle.api.domain.item.entity.Item;
import community.mingle.api.domain.member.entity.Country;
import community.mingle.api.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Collectors;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i join fetch i.member as m where i.member.university.country = :country")
    Page<Item> findAllByMemberCountry(Country country, PageRequest pageRequest);

}
