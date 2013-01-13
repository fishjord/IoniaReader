/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fishjord.ionia.jcr;

import common.Logger;
import fishjord.ionia.db.Manga;
import java.lang.reflect.Method;
import java.util.Calendar;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.jackrabbit.core.TransientRepository;

/**
 *
 * @author fishjord
 */
public class JCRUtils {

    public static void createNewRepo() throws Exception {
        Repository repo = new TransientRepository();
        Session session = repo.login(new SimpleCredentials("admin", "admin".toCharArray()));

        if (session == null) {
            System.exit(1);
        }

        try {
            Node root = session.getRootNode();
            Node n = root.addNode("manga");
            n = root.addNode("doujin");
            
            session.save();
        } finally {
            session.logout();
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
}
