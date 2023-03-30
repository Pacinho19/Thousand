package pl.pacinho.thousand.model.dto;

import lombok.Getter;

import java.util.Map;
import java.util.TreeMap;

@Getter
public class AuctionDto {

    private Map<String, AuctionOfferDto> offersMap;
    private Integer highestOffer;
    private Integer currentPLayer;

    public AuctionDto(Integer highestOffer, Integer currentPLayer) {
        this.highestOffer = highestOffer;
        this.currentPLayer = currentPLayer;
        this.offersMap= new TreeMap<>();
    }

    public void addOffer(String player, AuctionOfferDto offer) {
        if(!offer.pass())
            this.highestOffer = offer.value();
        this.offersMap.put(player, offer);
    }
}
