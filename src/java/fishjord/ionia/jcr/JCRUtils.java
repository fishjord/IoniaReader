/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.mangareader.jcr;

import fishjord.mangareader.db.Chapter;
import fishjord.mangareader.db.Manga;
import fishjord.mangareader.db.MangaUser;
import fishjord.mangareader.jcr.MangaDAO.DAOSession;
import fishjord.mangareader.upload.Upload;
import fishjord.mangareader.upload.UploadUtils;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
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
        RepositoryImpl impl = null;
        Session session = repo.login(new SimpleCredentials("manga", "manga".toCharArray()));
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
            Value value = prop.getValue();
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

    public static void add(String repoDir, String repoXml, String type, String zip) throws Exception {
        MangaDAO dao = new MangaDAO(repoDir, repoXml);

        System.out.print("username: ");
        String username = System.console().readLine();

        DAOSession session = dao.login(new MangaUser(username));

        Upload upload = UploadUtils.fromZip(new File(zip));
        session.persist(upload);
        session.logout();
    }

    private static void addTags() {
        String line;
        Set<String> tags = new HashSet();
        while ((line = System.console().readLine()) != null) {
            tags.add(line.trim());
        }

    }

    private static void dumpManga(String repoDir, String repoXml, String title) throws Exception {
        MangaDAO dao = new MangaDAO(repoDir, repoXml);

        System.out.print("username: ");
        String username = System.console().readLine();

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

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("USAGE: JCRUtils <command>");
            System.err.println("\tnew");
            System.err.println("\tadd_manga <repo_dir> <repository.xml> <zip>");
            System.err.println("\tadd_doujin <repo_dir> <repository.xml> <zip>");
            System.exit(1);
        }

        String cmd = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        if (cmd.equals("new")) {
            createNewRepo();
        } else if (cmd.equals("dump")) {
            dump(args[0], args[1]);
        } else if (cmd.equals("add_manga")) {
            add(args[0], args[1], "manga", args[2]);
        } else if (cmd.equals("add_doujin")) {
            add(args[0], args[1], "doujin", args[2]);
        } else if (cmd.equals("dump_manga")) {
            dumpManga(args[0], args[1], args[2]);
        } else if (cmd.equals("list")) {
            listManga(args[0], args[1]);
        } else {
            System.err.println("Unknown command '" + cmd + "'");
        }
    }
}
