package community.mingle.api.domain.report.entity;

import community.mingle.api.domain.item.entity.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Entity
@Table(name = "item_report")
public class ItemReport extends Report{

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

}
