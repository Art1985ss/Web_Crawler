package crawler;

import java.util.*;

public class LinkRepository {
    private final Set<Link> list = Collections.synchronizedSet(new HashSet<>());

    public synchronized void add(Link link) {
        list.add(link);
    }

    public Set<Link> getAll() {
        return list;
    }

    public void clear() {
        list.clear();
    }

    public int size() {
        return list.size();
    }

    public boolean contains(Link link) {
        return list.contains(link);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Link l : list) {
            sb.append(l).append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LinkRepository that = (LinkRepository) o;
        return list.equals(that.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(list);
    }
}
