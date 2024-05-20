package net.etfbl.pj2.rental;

import java.math.BigDecimal;
import java.util.Date;

public class Rental {

	private String rentalId;
	private String userId;
	private String userName;
	private Integer startLocation;
	private Integer endLocation;
	private Date startTime;
	private Date endTime;
	private Integer durationInSeconds;
	private BigDecimal price;
	private Boolean isWideArea;
	private Boolean isPromotion;
	private Boolean isDiscount;
	
	
	public String getRentalId() {
		return rentalId;
	}
	public void setRentalId(String rentalId) {
		this.rentalId = rentalId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Integer getStartLocation() {
		return startLocation;
	}
	public void setStartLocation(Integer startLocation) {
		this.startLocation = startLocation;
	}
	public Integer getEndLocation() {
		return endLocation;
	}
	public void setEndLocation(Integer endLocation) {
		this.endLocation = endLocation;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Integer getDurationInSeconds() {
		return durationInSeconds;
	}
	public void setDurationInSeconds(Integer durationInSeconds) {
		this.durationInSeconds = durationInSeconds;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public Boolean getIsWideArea() {
		return isWideArea;
	}
	public void setIsWideArea(Boolean isWideArea) {
		this.isWideArea = isWideArea;
	}
	public Boolean getIsPromotion() {
		return isPromotion;
	}
	public void setIsPromotion(Boolean isPromotion) {
		this.isPromotion = isPromotion;
	}
	public Boolean getIsDiscount() {
		return isDiscount;
	}
	public void setIsDiscount(Boolean isDiscount) {
		this.isDiscount = isDiscount;
	}
	
	
}
