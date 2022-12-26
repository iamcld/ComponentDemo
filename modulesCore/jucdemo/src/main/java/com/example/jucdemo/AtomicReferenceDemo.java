package com.example.jucdemo;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceDemo {

    public static void main(String[] arg) {
        User z3 = new User("z3", 23);
        User li4 = new User("li4", 24);
        AtomicReference<User> atomicReference = new AtomicReference<>();
        // 初始化z3对象为原子引用
        atomicReference.set(z3);

        // 将原子引用z3修改为li4,因为期望值之前没有被修改为z3,符合预期z3值，所以能修改成功
        System.out.println(atomicReference.compareAndSet(z3, li4) + " data = " + atomicReference.get().toString());

        // 将原子引用z3修改为li4,因为上面已经将原子引用修改为li4,期望值不符合z3,所以不能修改成功
        System.out.println(atomicReference.compareAndSet(z3, li4) + " data = " + atomicReference.get().toString());


    }


    private static class User {
        String uersName;
        int age;

        @Override
        public String toString() {
            return "User{" +
                    "uersName='" + uersName + '\'' +
                    ", age=" + age +
                    '}';
        }

        public void setUersName(String uersName) {
            this.uersName = uersName;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getUersName() {
            return uersName;
        }

        public int getAge() {
            return age;
        }

        public User(String userName, int age) {
            this.uersName = userName;
            this.age = age;
        }


    }
}
