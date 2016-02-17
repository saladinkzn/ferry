package ru.shadam.ferry.simple.integrationTest2;

/**
 * @author sala
 */
public class Pageable {
    private String sort;
    private int offset;
    private int count;

    public Pageable(String sort, int offset, int count) {
        this.sort = sort;
        this.offset = offset;
        this.count = count;
    }

    public String getSort() {
        return sort;
    }

    public int getOffset() {
        return offset;
    }

    public int getCount() {
        return count;
    }
}
