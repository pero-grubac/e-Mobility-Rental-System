package net.etfbl.pj2.model;

import static net.etfbl.pj2.support.Fixtures.at;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.etfbl.pj2.support.Fixtures;

class RentalTest {

	@Test
	void routeIsCalculatedWhenRentalIsCreated() {
		Rental rental = Fixtures.rental("A1", at(0, 0), at(7, 8), 5, false, false);

		assertEquals(16, rental.getRoute().size());
		assertEquals(at(0, 0), rental.getRoute().get(0));
		assertEquals(at(7, 8), rental.getRoute().get(15));
	}

	@Test
	void endTimeIsStartTimePlusDuration() {
		Rental rental = Fixtures.rental("A1", at(0, 0), at(1, 1), 75, false, false);

		assertEquals(Fixtures.START.plusSeconds(75), rental.getEndTime());
	}

	@Test
	void keepsItsFlagsAndHasNoDiscountUntilOneIsGiven() {
		Rental rental = Fixtures.rental("A1", at(0, 0), at(1, 1), 5, true, false);

		assertTrue(rental.isPromotion());
		assertFalse(rental.isBreakdown());
		assertFalse(rental.isDiscount());
		assertEquals("K1", rental.getUser().getName());
		assertFalse(rental.getUser().hasDocuments());

		rental.setDiscount(true);
		assertTrue(rental.isDiscount());
	}
}
