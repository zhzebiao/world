package algorithm;

/**
 * @author zhengzebiao
 * @date 2020/1/17 9:48
 */
public class Node<T> {
    private Node<T> nextNode = null;
    private T content;

    public Node(T content, Node<T> nextNode) {
        this.content = content;
        this.nextNode = nextNode;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }

    public void setNextNode(Node<T> nextNode) {
        this.nextNode = nextNode;
    }

    public Node<T> getNextNode() {
        return nextNode;
    }
}