import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

@OpenAPIDefinition( info = @Info(
        title="Api Quarkus social",
        version = "1.0",
        contact = @Contact(
                name = "Gustavo Assis",
                email = "gdwassis@gmail.com"),
        license = @License(
                name = "Apache 2.0",
                url = "https://www.apache.org/licenses/LICENSE-2.0.html")))
public class QuarkusSocialApplication extends Application {
}
