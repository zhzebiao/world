package algorithm;

import java.util.Scanner;
import java.util.StringJoiner;

/**
 * @author zhengzebiao
 * @date 2020/1/17 9:50
 */
public class LRUList<T> {
    private final int maxLength = 10;
    private int length = 0;
    private Node<T> head = null;

    private void add(T content) {
        Node<T> searchNode = searchNode(content);
        if (searchNode != null) {
            delete(searchNode);
            searchNode.setNextNode(head);
            head = searchNode;
        } else {
            head = new Node<>(content, head);
            length++;
            if (length > maxLength) {
                delete();
            }
        }
    }

    private Node<T> searchNode(T content) {
        Node<T> cursor = head;
        while (cursor != null) {
            if (content.equals(cursor.getContent())) {
                return cursor;
            }
            cursor = cursor.getNextNode();
        }
        return null;
    }

    private void delete() {
        Node<T> cursor = head;
        for (int i = 1; i < maxLength; i++) {
            cursor = cursor.getNextNode();
        }
        cursor.setNextNode(null);
        length--;
    }

    private void delete(Node<T> deleteNode) {
        if (head.getContent().equals(deleteNode.getContent())) {
            head = head.getNextNode();
            return;
        }
        Node<T> prevCursor = head;
        while (prevCursor.getNextNode() != null) {
            if (deleteNode.getContent().equals(prevCursor.getNextNode().getContent())) {
                prevCursor.setNextNode(prevCursor.getNextNode().getNextNode());
                return;
            }
            prevCursor = prevCursor.getNextNode();
        }
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        Node<T> cursor = head;
        while (cursor != null) {
            sj.add(cursor.getContent().toString());
            cursor = cursor.getNextNode();
        }
        return sj.toString();
    }

    public static void main(String[] args) {
        LRUList<Integer> lruList = new LRUList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            lruList.add(scanner.nextInt());
            System.out.println(lruList.toString());
        }
    }
}