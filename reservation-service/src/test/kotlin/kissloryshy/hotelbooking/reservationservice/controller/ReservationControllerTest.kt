package kissloryshy.hotelbooking.reservationservice.controller

import kissloryshy.hotelbooking.reservationservice.entity.Client
import kissloryshy.hotelbooking.reservationservice.entity.Room
import kissloryshy.hotelbooking.reservationservice.entity.dto.*
import kissloryshy.hotelbooking.reservationservice.mapper.ClientMapper
import kissloryshy.hotelbooking.reservationservice.mapper.RoomMapper
import kissloryshy.hotelbooking.reservationservice.service.ReservationService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mapstruct.factory.Mappers
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.math.BigDecimal
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@WebMvcTest(ReservationController::class)
class ReservationControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var reservationService: ReservationService

    @Test
    fun getCount() {
        val reservation1 = ReservationDto(Client(), Room(), LocalDate.now(), LocalDate.now(), LocalDate.now())
        val reservation2 = ReservationDto(Client(), Room(), LocalDate.now(), LocalDate.now(), LocalDate.now())
        val reservations = listOf(reservation1, reservation2)
        val reservationCountDto = ReservationCountDto(reservations.size.toLong())

        `when`(reservationService.getCount()).thenReturn(reservationCountDto)

        val request = MockMvcRequestBuilders
            .get("/api/reservations/getCount")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.reservationCount").value(reservationCountDto.reservationCount))

        Mockito.verify(reservationService, times(1)).getCount()
    }

    @Test
    fun getAll() {
        val reservation1 = ReservationDto(Client(), Room(), LocalDate.now(), LocalDate.now(), LocalDate.now())
        val reservation2 = ReservationDto(Client(), Room(), LocalDate.now(), LocalDate.now(), LocalDate.now())
        val reservations = listOf(reservation1, reservation2)

        `when`(reservationService.getAll(0, 5)).thenReturn(reservations)

        val request = MockMvcRequestBuilders
            .get("/api/reservations/getAll/0/5")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(reservations.size))

        Mockito.verify(reservationService, times(1)).getAll(0, 5)
    }

    @Test
    fun getByClientUsername() {
        val testUn = "holla-amigo"
        val clientDto = ClientDto(testUn, "testFn", "testLn", "test@mail.com", "+79000000000", LocalDate.now())
        val converter = Mappers.getMapper(ClientMapper::class.java)
        val reservationDto =
            ReservationDto(converter.toModel(clientDto), Room(), LocalDate.now(), LocalDate.now(), LocalDate.now())
        val reservations = listOf(reservationDto)

        `when`(reservationService.getByClientUsername(testUn)).thenReturn(reservations)

        val request = MockMvcRequestBuilders
            .get("/api/reservations/getByClientUsername/$testUn")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(reservations.size))
            .andExpect(jsonPath("$[0].client.username").value(testUn))

        verify(reservationService, times(1)).getByClientUsername(testUn)
    }

    @Test
    fun getByRoomNumber() {
        val testNumber = 7
        val roomDto = RoomDto(testNumber, 2, 2, true, BigDecimal(2000), BigDecimal(2800))
        val converter = Mappers.getMapper(RoomMapper::class.java)
        val reservationDto =
            ReservationDto(Client(), converter.toModel(roomDto), LocalDate.now(), LocalDate.now(), LocalDate.now())
        val reservations = listOf(reservationDto)

        `when`(reservationService.getByRoomNumber(testNumber)).thenReturn(reservations)

        val request = MockMvcRequestBuilders
            .get("/api/reservations/getByRoomNumber/$testNumber")
            .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.length()").value(reservations.size))
            .andExpect(jsonPath("$[0].room.number").value(testNumber))

        Mockito.verify(reservationService, times(1)).getByRoomNumber(testNumber)
    }

//    @Test
//    fun create_old() {
//        val date = LocalDate.now()
//
//        val reservationDto = ReservationDto(
//            Client(1, "kissloryshy", "lory", "kiss", "kissloryshy@gmail.com", "+79044488877", date.minusYears(20)),
//            Room(1, 1, 2, 2, true, BigDecimal(2500), BigDecimal(2850)),
//            date,
//            date,
//            date.plusDays(3)
//        )
//
//        mockMvc.perform(
//            MockMvcRequestBuilders
//                .post("/api/reservations/create")
//                .content(
//                    objectMapper.registerModule(JavaTimeModule())
//                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writeValueAsString(reservationDto)
//                )
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON)
//        )
//            .andExpect(status().isOk)
//    }
}
