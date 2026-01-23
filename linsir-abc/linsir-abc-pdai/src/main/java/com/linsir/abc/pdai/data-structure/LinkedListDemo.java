package com.linsir.abc.pdai.data-structure;

/**
 * 普通链表示例代码
 * 
 * 说明：
 * 1. 链表是一种线性数据结构，由一系列节点组成
 * 2. 链表的特点：
 *    - 元素在内存中不连续存储
 *    - 每个节点包含数据和指向下一个节点的指针
 *    - 插入和删除元素效率较高（时间复杂度O(1)）
 *    - 访问元素效率较低（时间复杂度O(n)）
 *    - 长度可以动态变化
 */
public class LinkedListDemo {

    /**
     * 链表节点类
     */
    private static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    /**
     * 单链表实现
     */
    private static class LinkedList {
        private Node head;
        private int size;

        LinkedList() {
            this.head = null;
            this.size = 0;
        }

        /**
         * 添加元素到链表末尾
         */
        public void add(int data) {
            Node newNode = new Node(data);
            if (head == null) {
                head = newNode;
            } else {
                Node current = head;
                while (current.next != null) {
                    current = current.next;
                }
                current.next = newNode;
            }
            size++;
        }

        /**
         * 在指定位置添加元素
         */
        public void add(int index, int data) {
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }

            Node newNode = new Node(data);
            if (index == 0) {
                newNode.next = head;
                head = newNode;
            } else {
                Node current = head;
                for (int i = 0; i < index - 1; i++) {
                    current = current.next;
                }
                newNode.next = current.next;
                current.next = newNode;
            }
            size++;
        }

        /**
         * 删除指定位置的元素
         */
        public int remove(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }

            int removedData;
            if (index == 0) {
                removedData = head.data;
                head = head.next;
            } else {
                Node current = head;
                for (int i = 0; i < index - 1; i++) {
                    current = current.next;
                }
                removedData = current.next.data;
                current.next = current.next.next;
            }
            size--;
            return removedData;
        }

        /**
         * 获取指定位置的元素
         */
        public int get(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }

            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            return current.data;
        }

        /**
         * 修改指定位置的元素
         */
        public void set(int index, int data) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }

            Node current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
            current.data = data;
        }

        /**
         * 检查链表是否包含指定元素
         */
        public boolean contains(int data) {
            Node current = head;
            while (current != null) {
                if (current.data == data) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }

        /**
         * 获取链表长度
         */
        public int size() {
            return size;
        }

        /**
         * 检查链表是否为空
         */
        public boolean isEmpty() {
            return size == 0;
        }

        /**
         * 清空链表
         */
        public void clear() {
            head = null;
            size = 0;
        }

        /**
         * 打印链表
         */
        public void print() {
            Node current = head;
            System.out.print("链表元素: ");
            while (current != null) {
                System.out.print(current.data + " -> ");
                current = current.next;
            }
            System.out.println("null");
        }
    }

    /**
     * 测试单链表
     */
    public void testLinkedList() {
        System.out.println("=== 单链表示例 ===");
        
        LinkedList list = new LinkedList();
        
        // 添加元素
        list.add(1);
        list.add(2);
        list.add(3);
        list.print();
        System.out.println("链表长度: " + list.size());
        
        // 在指定位置添加元素
        list.add(1, 4);
        list.print();
        System.out.println("链表长度: " + list.size());
        
        // 删除元素
        int removed = list.remove(2);
        System.out.println("删除的元素: " + removed);
        list.print();
        System.out.println("链表长度: " + list.size());
        
        // 获取元素
        System.out.println("索引1处的元素: " + list.get(1));
        
        // 修改元素
        list.set(0, 5);
        list.print();
        
        // 检查元素是否存在
        System.out.println("链表是否包含元素2: " + list.contains(2));
        System.out.println("链表是否包含元素6: " + list.contains(6));
        
        // 检查链表是否为空
        System.out.println("链表是否为空: " + list.isEmpty());
        
        // 清空链表
        list.clear();
        System.out.println("清空链表后，链表是否为空: " + list.isEmpty());
        System.out.println("链表长度: " + list.size());
    }

    /**
     * 双链表节点类
     */
    private static class DoublyNode {
        int data;
        DoublyNode prev;
        DoublyNode next;

        DoublyNode(int data) {
            this.data = data;
            this.prev = null;
            this.next = null;
        }
    }

    /**
     * 双链表实现
     */
    private static class DoublyLinkedList {
        private DoublyNode head;
        private DoublyNode tail;
        private int size;

        DoublyLinkedList() {
            this.head = null;
            this.tail = null;
            this.size = 0;
        }

        /**
         * 添加元素到链表末尾
         */
        public void add(int data) {
            DoublyNode newNode = new DoublyNode(data);
            if (head == null) {
                head = newNode;
                tail = newNode;
            } else {
                tail.next = newNode;
                newNode.prev = tail;
                tail = newNode;
            }
            size++;
        }

        /**
         * 在指定位置添加元素
         */
        public void add(int index, int data) {
            if (index < 0 || index > size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }

            if (index == 0) {
                // 添加到头部
                DoublyNode newNode = new DoublyNode(data);
                if (head == null) {
                    head = newNode;
                    tail = newNode;
                } else {
                    newNode.next = head;
                    head.prev = newNode;
                    head = newNode;
                }
            } else if (index == size) {
                // 添加到尾部
                add(data);
                return;
            } else {
                // 添加到中间
                DoublyNode newNode = new DoublyNode(data);
                DoublyNode current = head;
                for (int i = 0; i < index - 1; i++) {
                    current = current.next;
                }
                newNode.next = current.next;
                newNode.prev = current;
                current.next.prev = newNode;
                current.next = newNode;
            }
            size++;
        }

        /**
         * 删除指定位置的元素
         */
        public int remove(int index) {
            if (index < 0 || index >= size) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
            }

            int removedData;
            if (index == 0) {
                // 删除头部
                removedData = head.data;
                if (head == tail) {
                    head = null;
                    tail = null;
                } else {
                    head = head.next;
                    head.prev = null;
                }
            } else if (index == size - 1) {
                // 删除尾部
                removedData = tail.data;
                tail = tail.prev;
                tail.next = null;
            } else {
                // 删除中间
                DoublyNode current = head;
                for (int i = 0; i < index; i++) {
                    current = current.next;
                }
                removedData = current.data;
                current.prev.next = current.next;
                current.next.prev = current.prev;
            }
            size--;
            return removedData;
        }

        /**
         * 打印链表
         */
        public void print() {
            DoublyNode current = head;
            System.out.print("链表元素: ");
            while (current != null) {
                System.out.print(current.data + " <-> ");
                current = current.next;
            }
            System.out.println("null");
        }

        /**
         * 反向打印链表
         */
        public void printReverse() {
            DoublyNode current = tail;
            System.out.print("反向打印: ");
            while (current != null) {
                System.out.print(current.data + " <-> ");
                current = current.prev;
            }
            System.out.println("null");
        }

        /**
         * 获取链表长度
         */
        public int size() {
            return size;
        }
    }

    /**
     * 测试双链表
     */
    public void testDoublyLinkedList() {
        System.out.println("\n=== 双链表示例 ===");
        
        DoublyLinkedList list = new DoublyLinkedList();
        
        // 添加元素
        list.add(1);
        list.add(2);
        list.add(3);
        list.print();
        System.out.println("链表长度: " + list.size());
        
        // 在指定位置添加元素
        list.add(1, 4);
        list.print();
        System.out.println("链表长度: " + list.size());
        
        // 删除元素
        int removed = list.remove(2);
        System.out.println("删除的元素: " + removed);
        list.print();
        System.out.println("链表长度: " + list.size());
        
        // 反向打印
        list.printReverse();
    }

    /**
     * 链表的常见算法
     */
    public void linkedListAlgorithms() {
        System.out.println("\n=== 链表常见算法 ===");
        
        // 创建一个链表：1 -> 2 -> 3 -> 4 -> 5
        LinkedList list = new LinkedList();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        System.out.println("原始链表:");
        list.print();
        
        // 测试反转链表
        System.out.println("\n反转链表:");
        reverseLinkedList(list);
        list.print();
        
        // 测试查找中间节点
        System.out.println("\n查找中间节点:");
        int middle = findMiddleNode(list);
        System.out.println("中间节点的值: " + middle);
        
        // 测试检测环
        System.out.println("\n检测环:");
        boolean hasCycle = hasCycle(list);
        System.out.println("链表是否有环: " + hasCycle);
    }

    /**
     * 反转链表
     */
    private void reverseLinkedList(LinkedList list) {
        if (list.head == null || list.head.next == null) {
            return;
        }

        Node prev = null;
        Node current = list.head;
        Node next = null;

        while (current != null) {
            next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }

        list.head = prev;
    }

    /**
     * 查找中间节点（快慢指针法）
     */
    private int findMiddleNode(LinkedList list) {
        if (list.head == null) {
            throw new IllegalArgumentException("链表为空");
        }

        Node slow = list.head;
        Node fast = list.head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow.data;
    }

    /**
     * 检测链表是否有环（快慢指针法）
     */
    private boolean hasCycle(LinkedList list) {
        if (list.head == null || list.head.next == null) {
            return false;
        }

        Node slow = list.head;
        Node fast = list.head.next;

        while (slow != fast) {
            if (fast == null || fast.next == null) {
                return false;
            }
            slow = slow.next;
            fast = fast.next.next;
        }

        return true;
    }
}
