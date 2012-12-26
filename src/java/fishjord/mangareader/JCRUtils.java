/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader;

import java.util.Arrays;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import org.apache.jackrabbit.core.TransientRepository;

/**
 *
 * @author fishjord
 */
public class JCRUtils {
    
    private static void createNewRepo() throws Exception {
        Repository repo = new TransientRepository();
        Session session = repo.login(new SimpleCredentials("manga", "manga".toCharArray()));
        Node root = session.getRootNode();
        root.addNode("manga");
        root.addNode("doujin");
        
        session.save();        
    }
    
    public void addManga(String repoDir, String repoXml, String zip) {
        
    }
    
    public void addDoujin(String repoDir, String repoXml, String zip) {
        
    }
    
    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            System.err.println("USAGE: JCRUtils <command>");
            System.err.println("\tnew");
            System.err.println("\tadd_manga <repo_dir> <repository.xml> <zip>");
            System.err.println("\tadd_doujin <repo_dir> <repository.xml> <zip>");
            System.exit(1);
        }
        
        String cmd = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        
        if(cmd.equals("new")) {
            createNewRepo();
        } else if(cmd.equals("add_manga")) {
            addManga(args[0], args[1], args[2]);
        } else if (cmd.equals("add_doujin")) {
            addDoujin(args[0], args[1], args[2]);
        } else {
            System.err.println("Unknown command '" + cmd + "'");
        }
    }
}
