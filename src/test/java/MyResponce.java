import lombok.*;
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyResponce {
    private int code;
    private String message;
    private String type;
}
