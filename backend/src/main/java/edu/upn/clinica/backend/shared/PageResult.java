package edu.upn.clinica.backend.shared;

import java.util.List;

public class PageResult<T> {

    private List<T> content;
    private int     page;
    private int     size;
    private long    totalElements;
    private int     totalPages;

    public PageResult(List<T> content, long totalElements, int page, int size) {
        this.content       = content;
        this.totalElements = totalElements;
        this.page          = page;
        this.size          = size;
        this.totalPages    = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
    }

    public List<T> getContent()       { return content;       }
    public int     getPage()          { return page;          }
    public int     getSize()          { return size;          }
    public long    getTotalElements() { return totalElements; }
    public int     getTotalPages()    { return totalPages;    }
    public boolean isFirst()          { return page == 0;     }
    public boolean isLast()           { return page >= totalPages - 1; }
}