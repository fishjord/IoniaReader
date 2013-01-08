/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.jcr;

import fishjord.ionia.db.Manga;
import fishjord.ionia.db.MangaUser;
import fishjord.ionia.jcr.MangaDAO.DAOSession;
import fishjord.ionia.upload.Upload;
import fishjord.ionia.upload.UploadUtils;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.config.RepositoryConfig;

/**
 *
 * @author fishjord
 */
public class JCRUtils {

    private static void createNewRepo() throws Exception {
        Repository repo = new TransientRepository();
        Session session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));

        if (session == null) {
            System.exit(1);
        }

        try {
            Node root = session.getRootNode();
            Node n = root.addNode("manga");
            n = root.addNode("doujin");

            dump(root, 0, false);

            session.save();
        } finally {
            session.logout();
        }
    }

    public static void dump(String repoDir, String repoXml) throws Exception {
        RepositoryConfig config = RepositoryConfig.create(repoXml, repoDir);
        Repository repo = RepositoryImpl.create(config);

        Session sess = repo.login();

        dump(sess.getRootNode(), 0, false);
        sess.logout();
    }

    private static void dump(Node n, int depth, boolean dumpAll) throws Exception {
        StringBuilder tab = new StringBuilder();
        for (int index = 0; index < depth * 4; index++) {
            tab.append(" ");
        }

        if (n.getName().startsWith("jcr:") && !dumpAll) {
            return;
        }

        System.out.print(tab);
        System.out.print(n.getName());
        System.out.print(" ");
        PropertyIterator iter = n.getProperties();
        while (iter.hasNext()) {
            Property prop = iter.nextProperty();
            if ((prop.getName().startsWith("jcr:") && !dumpAll)) {
                continue;
            }
            System.out.print(prop.getName() + "=");
            if (prop.getType() == PropertyType.BINARY) {
                System.out.print("<BINARY> ");
                continue;
            }

            if (prop.isMultiple()) {
                System.out.print("[");
                for (Value value : prop.getValues()) {
                    System.out.print(value.getString() + ",");
                }
                System.out.print("]");
            } else {
                System.out.print(prop.getValue().getString());
            }

            System.out.print(" ");
        }

        System.out.println();

        if (n.getName().equals("pages")) {
            dumpAll = true;
        }

        NodeIterator nodeIter = n.getNodes();
        while (nodeIter.hasNext()) {
            dump(nodeIter.nextNode(), depth + 1, dumpAll);
        }
    }

    public static void setProperties(Node addTo, Object o) {
        Class clazz = o.getClass();

        for (Method method : clazz.getMethods()) {
            if (method.getParameterTypes().length > 0) {
                continue;
            }

            Class rettype = method.getReturnType();
            if (rettype == Void.class) {
                continue;
            }

            String name = method.getName();
            if (name.startsWith("get")) {
                name = name.substring(3);
            } else if (name.startsWith("is")) {
                name = name.substring(2);
            } else {
                continue;
            }

            try {
                clazz.getMethod("set" + name, rettype);
            } catch (NoSuchMethodException e) {
                continue;
            }

            try {
                Object value = method.invoke(o, null);
                if (value == null) {
                    continue;
                }

                if (rettype == Calendar.class) {
                    addTo.setProperty(name, (Calendar) value);
                } else if (rettype == String.class) {
                    addTo.setProperty(name, (String) value);
                } else if (rettype == Integer.class) {
                    addTo.setProperty(name, (Integer) value);
                } else if (rettype == Long.class) {
                    addTo.setProperty(name, (Long) value);
                } else if (rettype == Character.class) {
                    addTo.setProperty(name, (Character) value);
                } else if (rettype == Short.class) {
                    addTo.setProperty(name, (Short) value);
                } else if (rettype == Byte.class) {
                    addTo.setProperty(name, (Byte) value);
                } else if (rettype == Double.class) {
                    addTo.setProperty(name, (Double) value);
                } else if (rettype == Float.class) {
                    addTo.setProperty(name, (Float) value);
                } else if (rettype == Boolean.class) {
                    addTo.setProperty(name, (Boolean) value);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void getProperties(Node addTo, Object o) throws RepositoryException {
        Class clazz = o.getClass();
        for (Property prop : JcrUtils.getProperties(addTo)) {
            String name = prop.getName();
            if(prop.isMultiple()) {
                continue;
            }
            try {
                Method getter = null;
                try {
                    getter = clazz.getMethod("get" + name);
                    getter = clazz.getMethod("is" + name);
                } catch (Exception e) {
                }

                if (getter == null) {
                    continue;
                }
                Value value = prop.getValue();

                Class rettype = getter.getReturnType();
                Method setter = clazz.getMethod("set" + name, rettype);

                if (rettype == Calendar.class) {
                    setter.invoke(o, value.getDate());
                } else if (rettype == String.class) {
                    setter.invoke(o, value.getString());
                } else if (rettype == Integer.class) {
                    setter.invoke(o, (int) value.getLong());
                } else if (rettype == Long.class) {
                    setter.invoke(o, (int) value.getLong());
                } else if (rettype == Character.class) {
                    setter.invoke(o, (char) value.getLong());
                } else if (rettype == Short.class) {
                    setter.invoke(o, (short) value.getLong());
                } else if (rettype == Byte.class) {
                    setter.invoke(o, (byte) value.getLong());
                } else if (rettype == Double.class) {
                    setter.invoke(o, value.getDouble());
                } else if (rettype == Float.class) {
                    setter.invoke(o, (float) value.getDouble());
                } else if (rettype == Boolean.class) {
                    setter.invoke(o, value.getBoolean());
                }

            } catch (Exception e) {
            }
        }
    }

    public static void add(String repoDir, String repoXml, String type, List<String> zips) throws Exception {
        MangaDAO dao = new MangaDAO(repoDir, repoXml);

        //System.out.print("username: ");
        //String username = System.console().readLine();
	String username = "";

        DAOSession session = dao.login(new MangaUser(username));

	for(String zip : zips) {
	    try {
		Upload upload = UploadUtils.fromZip(new File(zip));
		session.persist(upload);
		System.err.println("Added " + zip + " successfully");
	    } catch(Exception e) {
		System.err.println("Failed to persist " + zip + ": " + e.getMessage());
	    }
	}
        session.logout();
    }

    private static void addTags(String repoDir, String repoXml, Set<String> tags) throws Exception {
        MangaDAO dao = new MangaDAO(repoDir, repoXml);
        System.out.print("username: ");
        String username = System.console().readLine();

        DAOSession session = dao.login(new MangaUser(username));

        tags.addAll(session.getAllTags());

        session.setTags(tags);
        session.logout();
    }

    private static void resetTags(String repoDir, String repoXml) throws Exception {
        MangaDAO dao = new MangaDAO(repoDir, repoXml);
        System.out.print("username: ");
        String username = System.console().readLine();

        DAOSession session = dao.login(new MangaUser(username));

        session.setTags(new HashSet());
        session.logout();
    }

    private static void listTags(String repoDir, String repoXml) throws Exception {
        MangaDAO dao = new MangaDAO(repoDir, repoXml);
        System.out.print("username: ");
        String username = System.console().readLine();

        DAOSession session = dao.login(new MangaUser(username));

        System.out.println(session.getAllTags());
        session.logout();
    }

    private static void dumpManga(String repoDir, String repoXml, String title) throws Exception {
        MangaDAO dao = new MangaDAO(repoDir, repoXml);

        //System.out.print("username: ");
        //String username = System.console().readLine();
	String username = "";

        DAOSession session = dao.login(new MangaUser(username));
        Manga m = session.getManga(title);

        System.out.println(m);

        session.logout();
    }

    private static void listManga(String repoDir, String repoXml) throws Exception {
        MangaDAO dao = new MangaDAO(repoDir, repoXml);

        System.out.print("username: ");
        String username = System.console().readLine();

        DAOSession session = dao.login(new MangaUser(username));
        for (Manga m : session.listManga()) {
            System.out.println(m);
        }

        session.logout();
    }

    private static void printUsageAndExit() {
        System.err.println("USAGE: JCRUtils <command>");
        System.err.println("\tnew");
        System.err.println("\tadd_manga <repo_dir> <repository.xml> [zip] - load manga from zip file(s), from stdin or command line");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            printUsageAndExit();
        }

        String cmd = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        if (cmd.equals("new")) {
            createNewRepo();
        } else if (cmd.equals("dump")) {
            if (args.length != 2) {
                printUsageAndExit();
            }
            dump(args[0], args[1]);
        } else if (cmd.equals("add_manga")) {
	    List<String> toAdd = new ArrayList();
            if (args.length > 2) {
                for (int index = 2; index < args.length; index++) {
		    toAdd.add(args[index]);
                }
            } else if (args.length == 2) {
                String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while ((line = reader.readLine()) != null) {
                    toAdd.add(line);
                }
            } else {
                printUsageAndExit();
            }
	    System.err.println("Adding " + toAdd.size() + " manga");
	    add(args[0], args[1], "manga", toAdd);
        } else if (cmd.equals("dump_manga")) {
            if (args.length != 3) {
                printUsageAndExit();
            }
            dumpManga(args[0], args[1], args[2]);
        } else if (cmd.equals("list")) {
            if (args.length != 2) {
                printUsageAndExit();
            }
            listManga(args[0], args[1]);
        } else if (cmd.equals("add_tags")) {
            String line;
            Set<String> tags = new HashSet();
            BufferedReader reader = null;
            if (args.length == 2) {
                reader = new BufferedReader(System.console().reader());
            } else if (args.length == 3) {
                reader = new BufferedReader(new FileReader(args[2]));
            } else {
                printUsageAndExit();
            }

            while ((line = reader.readLine()) != null) {
                tags.add(line.trim());
            }

            addTags(args[0], args[1], tags);

        } else if (cmd.equals("reset_tags")) {
            if (args.length != 2) {
                printUsageAndExit();
            }

            resetTags(args[0], args[1]);
        } else if (cmd.equals("list_tags")) {
            if (args.length != 2) {
                printUsageAndExit();
            }

            listTags(args[0], args[1]);
        } else {
            System.err.println("Unknown command '" + cmd + "'");
            printUsageAndExit();
        }
    }
}
