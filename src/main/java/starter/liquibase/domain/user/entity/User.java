package starter.liquibase.domain.user.entity;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Yoo Ju Jin(jujin1324@daum.net)
 * Created Date : 2022/09/10
 */

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "uuid", columnDefinition = "BINARY(16)", updatable = false)
    private UUID uuid;

    @Column(name = "name")
    private String name;

    // TODO: liquibase version control
//    @Column(name = "phone_no")
//    private String phoneNo;
}
