package PFE.Gestion_Des_Anonces.Api.Models.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    @Query(value="SELECT count(*) FROM reservation  " +
            "WHERE id_anonce = :id " +
            "AND status = 3 "+
            "AND ( date_reservation_arrive between :date1 and :date2  " +
            "OR date_reservation_depart between :date1 and :date2 )"
            , nativeQuery = true
    )
    int countReservations(long id ,LocalDate date1,LocalDate date2);
}
