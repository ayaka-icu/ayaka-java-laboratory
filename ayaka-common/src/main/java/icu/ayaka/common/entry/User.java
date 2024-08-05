package icu.ayaka.common.entry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    private String name;

    private String password;

    private String nickname;

    private String email;

    private Date createTime;

    private boolean admin;

    public static User getTestUser(){
        return new User(1L, "ayaka", "521", "ayaka-icu", "admin", new Date(), true);
    }

}
