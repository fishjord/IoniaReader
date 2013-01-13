/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.cli;

import fishjord.ionia.db.Chapter;
import fishjord.ionia.db.Manga;
import fishjord.ionia.db.Page;
import fishjord.ionia.jcr.JCRUtils;
import fishjord.ionia.jcr.MangaDAO;
import fishjord.ionia.jcr.MangaDAO.DAOSession;
import fishjord.ionia.upload.Upload;
import fishjord.ionia.upload.UploadUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author fishjord
 */
public class JCRShell {

    public static void add(DAOSession session, String type, List<String> zips) throws Exception {
        for (String zip : zips) {
            try {
                Upload upload = UploadUtils.fromZip(new File(zip));
                session.persist(upload);
                System.err.println("Added " + zip + " successfully");
            } catch (Exception e) {
                System.err.println("Failed to persist " + zip + ": " + e.getMessage());
            }
        }
    }

    public static void addTags(DAOSession session, Set<String> tags) throws Exception {
        tags.addAll(session.getAllTags());
        session.setTags(tags);
    }

    public static void resetTags(DAOSession session) throws Exception {
        session.setTags(new HashSet());
    }

    public static void listTags(MangaDAO.DAOSession session) throws Exception {
        System.out.println(session.getAllTags());
    }

    public static void dumpManga(MangaDAO.DAOSession session, String id) throws Exception {
        Manga m = session.getManga(id);
        System.out.println(m);
    }

    public static void listManga(MangaDAO.DAOSession session) throws Exception {
        for (Manga m : session.listManga()) {
            System.out.println(m);
        }
    }

    private static void resortChapterPages(DAOSession session, String mangaId) {
        if (mangaId == null) {
            for (Manga m : session) {
            }
        } else {
            Manga m = session.getManga(mangaId);

            for (Chapter c : m.getChapters()) {
                System.out.println(c.getChapterTitle());
                System.out.println("\t" + c.getPages());
                Collections.sort(c.getPages(), new Comparator<Page>() {
                    public int compare(Page o1, Page o2) {
                        return String.CASE_INSENSITIVE_ORDER.compare(o1.getId(), o2.getId());
                    }
                });
                System.out.println("\t" + c.getPages());
            }
        }
    }

    private static void printUsage() {
        System.err.println("USAGE: JCRUtils <command>");
        System.err.println("\tnew");
        System.err.println("\tadd_manga <repo_dir> <repository.xml> [zip] - load manga from zip file(s), from stdin or command line");
        System.err.println("\tdump <repo_dir> <repository.xml>");
        System.err.println("\tdump_manga <repo_dir> <repository.xml>");
        System.err.println("\tadd_tags <repo_dir> <repository.xml>");
        System.err.println("\treset_tags <repo_dir> <repository.xml>");
        System.err.println("\tlist_tags <repo_dir> <repository.xml>");
        System.err.println("\tresort_pages <repo_dir> <repository.xml> [manga_id]");
    }

    private static void processCommand(String[] args) throws Exception {
        if (args.length < 1) {
            printUsage();
            return;
        }

        String cmd = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        if (cmd.equals("new")) {
            JCRUtils.createNewRepo();
        } else {
            if (args.length < 2) {
                printUsage();
                return;
            }
            MangaDAO dao = new MangaDAO(args[0], args[1]);
            DAOSession session = dao.login("cli_shell");
            args = Arrays.copyOfRange(args, 2, args.length);

            processCommand(cmd, args, session);
            session.logout();
        }
    }

    private static void processCommand(String cmd, String[] args, DAOSession session) throws Exception {
        if (cmd.equals("new")) {
            System.err.println("Cannot create a new repository when already connected to one");
        } else if (cmd.equals("add_manga")) {
            List<String> toAdd = new ArrayList();
            if (args.length > 0) {
                for (int index = 0; index < args.length; index++) {
                    toAdd.add(args[index]);
                }
            } else if (args.length == 0) {
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while ((line = reader.readLine()) != null) {
                    if (line.equals(".")) {
                        break;
                    }
                    toAdd.add(line);
                }
            } else {
                printUsage();
                return;
            }
            System.err.println("Adding " + toAdd.size() + " manga");
            add(session, "manga", toAdd);
        } else if (cmd.equals("dump_manga")) {
            if (args.length != 1) {
                printUsage();
                return;
            }
            dumpManga(session, args[0]);
        } else if (cmd.equals("list")) {
            if (args.length != 0) {
                printUsage();
                return;
            }
            listManga(session);
        } else if (cmd.equals("add_tags")) {
            String line;
            Set<String> tags = new HashSet();
            BufferedReader reader;
            if (args.length == 0) {
                reader = new BufferedReader(new InputStreamReader(System.in));
            } else if (args.length == 1) {
                reader = new BufferedReader(new FileReader(args[0]));
            } else {
                printUsage();
                return;
            }

            while ((line = reader.readLine()) != null) {
                tags.add(line.trim());
            }

            addTags(session, tags);

        } else if (cmd.equals("reset_tags")) {
            if (args.length != 0) {
                printUsage();
                return;
            }

            resetTags(session);
        } else if (cmd.equals("list_tags")) {
            if (args.length != 0) {
                printUsage();
                return;
            }

            listTags(session);
        } else if (cmd.equals("resort_pages")) {
            if (args.length != 1) {
                printUsage();
                return;
            }

            resortChapterPages(session, args[0]);
        } else {
            System.err.println("Unknown command '" + cmd + "'");
            printUsage();
        }
    }

    public static void main(String[] incmd) throws Exception {
        if (incmd.length > 0) {
            processCommand(incmd);
            return;
        }

        String line;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        MangaDAO dao = null;
        DAOSession session = null;

        System.out.print("> ");
        while ((line = reader.readLine()) != null) {
            String[] lexemes = line.split("\\s+");
            String cmd = lexemes[0];
            String[] args = Arrays.copyOfRange(lexemes, 1, lexemes.length);

            if (cmd.equals("connect")) {
                if (args.length != 2) {
                    System.err.println("USAGE: connect <repository dir> <repository xml>");
                    continue;
                }

                if (dao != null) {
                    System.err.println("Already connected, close first");
                    continue;
                }

                dao = new MangaDAO(args[0], args[1]);
            } else if (cmd.equals("login")) {

                if (dao == null) {
                    System.err.println("Not connected to repository, connect first");
                    continue;
                }

                if (session != null) {
                    System.err.println("Session already open, logout first");
                    continue;
                }

                if (args.length == 0) {
                    session = dao.getAnonSession();
                } else if (args.length == 1) {
                    session = dao.login(args[0]);
                } else {
                    System.err.println("USAGE: login [username]");
                }
            } else if (cmd.equals("logout")) {
                if (session == null) {
                    System.err.println("Not logged in");
                    continue;
                }

                if (session != dao.getAnonSession()) {
                    session.logout();
                }

                session = null;
            } else if (cmd.equals("close")) {
                if (dao == null) {
                    System.err.println("Not connected");
                    continue;
                }

                dao.close();
                dao = null;
            } else {
                if (session != null) {
                    processCommand(cmd, args, session);
                } else if (dao != null) {
                    processCommand(cmd, args, dao.getAnonSession());
                } else {
                    processCommand(lexemes);
                }
            }
            System.out.print("> ");
        }

        if (session != null) {
            session.logout();
        }
    }
}
