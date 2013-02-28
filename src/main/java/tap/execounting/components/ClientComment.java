package tap.execounting.components;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.mediators.interfaces.ClientMed;
import tap.execounting.entities.Comment;
import java.util.Date;

/**
 * User: truth0
 * Date: 2/28/13
 * Time: 12:06 PM
 */
@Import(stylesheet = "context:css/components/ClientComment.css")
public class ClientComment {
    @Parameter(required = true) @Property
    private int clientId;
    @Inject
    private ClientMed clientMed;

    void setupRender(){
        clientMed.setUnitId(clientId);
    }
    public String getComment(){
        Comment c = clientMed.getComment();
         return c == null ? "" : c.getText();
    }
    public void setComment(String comment){
        clientMed.comment(comment, new Date().getTime());
    }
    public Date getCommentDate(){
        return clientMed.getComment().getDate();
    }
}
