package com.linsir.abc.pdai.collection;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.PriorityQueue;
import java.util.TreeSet;

public class CollectionAboutTest {


   // CollectionAbout collectionAbout = new CollectionAbout();

    private static Logger logger = LoggerFactory.getLogger(CollectionAboutTest.class);

    /*
    * HashSet和TreeSet都是Java中的集合类，它们的主要区别在于底层数据结构和元素的排序方式。

        底层数据结构：HashSet使用哈希表作为底层数据结构，而TreeSet使用红黑树作为底层数据结构。

        元素的排序方式：HashSet中的元素是无序的，而TreeSet中的元素是有序的，且默认按照元素的自然顺序排序。如果需要按照其他方式排序，则需要在创建TreeSet时指定一个Comparator对象。

        元素的唯一性：HashSet中的元素是唯一的，不允许重复，而TreeSet中的元素也是唯一的，但是它是通过比较器或元素的自然顺序来判断元素是否相同的。

        性能：HashSet的插入、删除和查找操作的时间复杂度都是O(1)，而TreeSet的这些操作的时间复杂度都是O(log n)。

        因此，如果需要快速的插入、删除和查找操作，并且不需要元素有序，则可以选择HashSet。如果需要元素有序，或者需要按照其他方式进行排序，则可以选择TreeSet。
    * */





    /*
    * 实现了NavigableSet接口，意味着它支持一系列的导航方法。比如查找与指定目标最匹配项。
      继承于AbstractSet，所以它是一个Set集合，具有Set的属性和方法。
      实现了Cloneable接口，意味着它能被克隆。
      实现了java.io.Serializable接口，意味着它支持序列化。
    *
    * */
    @Test
    public void  treeSetTest()
    {
        TreeSet<Integer> set = new TreeSet<>();
        set.add(5);
        set.add(4);
        set.add(5);
        set.add(3);
        set.add(1);
        set.add(9);
        System.out.print("正序遍历：" );
        set.forEach(item -> {
            System.out.print(item + "  ");
        });
        System.out.println();

        //逆序遍历
        System.out.print("逆序遍历：" );
        set.descendingIterator().forEachRemaining(item -> {
            System.out.print(item  + "  ");
        });
        System.out.println();


        TreeSet<Person> personTreeSet  =  new TreeSet<>();

        personTreeSet.add(new Person("admin","123"));
        personTreeSet.add(new Person("yyg","bb"));
        personTreeSet.add(new Person("jack","123"));
        personTreeSet.add(new Person("rose123","123"));
        personTreeSet.add(new Person("admin","123"));
        personTreeSet.add(new Person("xksss6","abc"));

        personTreeSet.forEach(person -> {
            logger.info(person.toString());
        });

        System.out.println();
    }


    @Test
    public void  hashSetTest()
    {
        logger.info("\n HashSet的插入、删除和查找操作的时间复杂度都是O(1)，而TreeSet的这些操作的时间复杂度都是O(log n)。\n ");
    }


    @Test
    public void  linkedHashSetTest()
    {
        logger.info("\n 具有 HashSet 的查找效率，且内部使用双向链表维护元素的插入顺序。\n");
    }

    @Test
    public void  priorityQueueTest()
    {
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(12);

        for (int i = 0; i < 10; i++)
        {
            priorityQueue.add((int)(Math.random()*10));
        }

        for (int i = 0; i < 10; i++)
        {
          System.out.println(priorityQueue.poll());
        }
    }

}
