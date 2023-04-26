package de.iks.rataplan.dto;

import de.iks.rataplan.domain.AppointmentRequestConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;

import javax.persistence.Entity;
import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
public class ParticipantAppointmentRequestDTO implements Serializable {

    private static final long serialVersionUID = 8169186536220940206L;
    private Integer id;
    private String title;
    private String description;
    private String organizerName;
    private String organizerMail;
    private Date deadline;
    private Integer userId;
    private boolean expired;
    private String participationToken;
    private AppointmentRequestConfig appointmentRequestConfig = new AppointmentRequestConfig();
    private List<String> consigneeList;
    private List<AppointmentDTO> appointments;
    private List<AppointmentMemberDTO> appointmentMembers;

}
