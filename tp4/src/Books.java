public class Books {
    private int id;
    private String title;
    private String author;
    private int borrowedby;
    private boolean available;

    public Books(int id, String title, String author, boolean available, int borrowedby) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.available = available;
        this.borrowedby = borrowedby;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getBorrowedby() {
        return borrowedby;
    }

    public void setBorrowedby(int borrowedby) {
        this.borrowedby = borrowedby;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "Books [id=" + id + ", title=" + title + ", author=" + author + ", borrowedby=" + borrowedby
                + ", available=" + available + "]";
    }
}
