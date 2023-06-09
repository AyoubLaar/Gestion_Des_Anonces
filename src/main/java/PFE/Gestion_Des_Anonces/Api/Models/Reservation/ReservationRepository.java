package PFE.Gestion_Des_Anonces.Api.Models.Reservation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    @Query(value="SELECT * FROM reservation  " +
            "WHERE id_anonce = :id " +
            "AND status = 4 "+
            "AND ( NOT(:date1 < date_reservation_arrive and :date2 < date_reservation_arrive)  " +
            "AND  NOT(date_reservation_depart < :date1 and date_reservation_depart < :date2 ) )  "
            , nativeQuery = true
    )
    List<Reservation> countReservations(long id , LocalDate date1, LocalDate date2);
}
