Fundamental Programming Principles
==================================

.. contents:: Table of Contents
  :local:

This project was intended as an exploration and demonstration of fundamental programming principles in the following areas:

- Input Validation
- Proper use of exceptions
- Fundamental Logging

Defensive Programming
---------------------

A good place to start is in Chapter 8 of the book `Code Complete`_, under Defensive Programming:

  In school you might have heard the expression, "Garbage in, garbage out." That expression is essentially software development's version of caveat emptor: let the user beware.

  For production software, garbage in, garbage out isn't good enough. A good program never puts out garbage, regardless of what it takes in. A good program uses "garbage in, nothing out," "garbage in, error message out," or "no garbage allowed in" instead.

  By today's standards, "garbage in, garbage out" is the mark of a sloppy, nonsecure program.

Robustness vs Correctness
-------------------------

There are two major principles that we must balance when designing and implementing defensive code. The following is another quote from the same book:

  Developers tend to use these terms informally, but, strictly speaking, these terms are at opposite ends of the scale from each other. *Correctness* means never returning an inaccurate result; returning no result is better than returning an inaccurate result. *Robustness* means always trying to do something that will allow the software to keep operating, even if that leads to results that are inaccurate sometimes.

  Safety-critical applications tend to favor correctness to robustness. It is better to return no result than to return a wrong result. The radiation machine is a good example of this principle.

  Consumer applications tend to favor robustness to correctness. Any result whatsoever is usually better than the software shutting down. The word processor I'm using occasionally displays a fraction of a line of text at the bottom of the screen. If it detects that condition, do I want the word processor to shut down? No. I know that the next time I hit Page Up or Page Down, the screen will refresh and the display will be back to normal.

Balancing these two principles is vital while coding functions and methods.

Design By Contract
------------------

Functions or methods are the fundamental building blocks of any software application and we should strive to `design them by contract <https://www.cs.umd.edu/class/fall2002/cmsc214/Projects/P1/proj1.contract.html>`_. This implies that a method must guarantee all its preconditions. During the execution of the code must ensure its invariants are hold and finally, when finished, it must guarantee a number of postconditions. Failure to do any of this should be signaled immediately.

* **Precondition**:  a condition that must be true of the parameters of a method and/or data members, if the method is to behave correctly, prior to running the code in the method.
* **Postcondition**: a condition that is true after running the code in a method.
* **Class Invariant**: a condition that is true before and after running the code in a method (except constructors and destructors).

Therefore a method contract states what input the method receives, and establishes a set of constraints or preconditions that such input must satisfy. The method contract also states what the method does, in other words what task is logically performed by the method and what results can be expected, what side effects should have happened, etc. There is also a number of postconditions that a method contract must guarantee upon successful completion of the method, in other words, guaranteed expectations about the final state of the world after the method is done running.

Any deviation of the contract must throw an exception or signal the problem somehow, which is the way to make obvious that either the preconditions were not satisfied, or the postconditions could not be met for any reason.

Failure to validate both the preconditions and postconditions of a method contract can leave a system in an inconsistent state in which the program results can no longer be guaranteed and where errors can happen unexpectedly due to the inconsistent state of the data.

We typically document the contract in the interface method documentation. For example, consider the following example from a ``BankAccount`` interface (intentionally oversimplified to make examples simpler to understand):

.. code-block:: java

 public interface BankAccount {

    /**
     * Withdrawing money from a savings account reduces its balance by the
     * provided withdrawal amount.
     *
     * For the withdrawal operation to succeed, the savings account is expected to have enough balance
     * to satisfy the withdrawal.
     *
     * At any point in time the final balance of the saving accounts may
     * never be smaller than 0.
     *
     * @param amount - the amount you want to withdraw from your account.
     * @return the balance in the account after the withdrawal.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     * @throws InsufficientFundsException if the current {@code balance} is smaller than {@code amount}
     */
    double withdrawMoney(double amount);

    /**
     * Saving money into the savings account increases its balance by the saved amount.
     *
     * In order that the saving succeed, the final account balance must represent a positive amount of money
     *
     * At any point in time the final balance of the saving accounts may never be smaller than 0.
     *
     * @param amount - the amount to save into the account.
     * @return the balance of the account after savings.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     */
    double saveMoney(double amount);
 }

The implementation class of this interface then must satisfy everything stated in the contract of its methods and our test classes must strive to satisfy those contracts.

Consider another example: let's say you defining  a ``Fraction`` class to represent that mathematical concept. You may need to follow a contract with the following rules:

* **Precondition**: the denominator must never be ``0``.
* **Invariant**: fractions will be kept in reduced form (i.e. ``2/3`` instead of ``6/9``, ``6`` instead of ``6/1``, ``0`` instead of ``0/2``)
* **Postcondition**: a fraction with a denominator of ``1`` will be represented as a whole number, not as a fraction (i.e. ``2`` instead of ``2/1``).
* **Postcondition**: a numerator of 0 will be represented as the whole number ``0``, not as a fraction (i.e. ``0`` instead of ``0/2``).

The **principle here** is that you may want to do the effort of documenting your interface contracts such that developers creating implementation make sure the contract holds at all times in their implementation and in their unit tests.

Once you have a contract properly defined you can **write tests to verify your contracts**:

.. code-block:: java

 public class SavingsAccountTest {

    private final AccountNumber accountNumber = new AccountNumber("1-234-567-890");
    private final BankAccount bankAccount = new SavingsAccount(accountNumber);

    @Test
    public void saveMoney() {
        double balance = bankAccount.saveMoney(100);
        assertThat(balance).isEqualTo(100);
        balance = bankAccount.saveMoney(75);
        assertThat(balance).isEqualTo(175);
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveMoneyWithNegativeAmount() {
        bankAccount.saveMoney(-100);
        Assert.fail("Savings of negative numbers should fail!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveMoneyWithZeroAmount() {
        bankAccount.saveMoney(0.0);
        Assert.fail("Savings of $0 should fail!");
    }

    @Test
    public void withdrawMoney() {
        double balance = bankAccount.saveMoney(100);
        assertThat(balance).isEqualTo(100);
        balance = bankAccount.withdrawMoney(50);
        assertThat(balance).isEqualTo(50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withdrawMoneyWithNegativeAmount() {
        bankAccount.withdrawMoney(-100);
        Assert.fail("Withdrawal of negative numbers should fail!");
    }

    @Test(expected = IllegalArgumentException.class)
    public void withdrawMoneyWithZeroAmount() {
        bankAccount.withdrawMoney(0.0);
        Assert.fail("Withdrawal of negative numbers should fail!");
    }

    @Test(expected = InsufficientFundsException.class)
    public void withdrawMoneyWithInsufficientFunds() {
        bankAccount.withdrawMoney(50);
        Assert.fail("Withdrawal should fail when there aren't sufficient funds!");
    }
 }

If you're following TDD style, you need not have implemented the ``SavingsAccount`` class and initially all tests would fail and gradually start passing as the methods are implemented properly one by one in the class.

Beware of Constructor Parameters
--------------------------------

Perhaps the most classical example of this kind of thing is the failure to properly validate the nullability of a method argument, particularly when it happens in a constructor. For example, consider this class:

.. code-block:: java

 class Foo {
   private final Bar bar;

   Foo(Bar bar) { this.bar = bar; } //Uh oh, no nullability checks!
   Bar getBar() { return this.bar; }
 }


Then at **some other time** and **some other place**, **somebody else** does:

.. code-block:: java

  Bar bar = null;
  Foo foo = new Foo(bar); //Uh oh, invalid data set
  someOtherObj.passMeSomeFoo(foo);


And ``someOtherObj`` will store this ``foo`` instance for a while, waiting for some event to happen **later** and when somebody does this and gets an unexpected failure:

.. code-block:: java

  foo.getBar().getName(); //NullPointerException


The problem here is that the spatial (where) and temporal (when) locations of the exception thrown here are very far away from the source of the problem (i.e. the constructor above). No wonder why Tony Hoare called his invention of null references `a billion dollars mistake <https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare>`_. However, this temporality and spatiality issue may happen with other forms of unvalidated data.

To make matters worse, in a distributed system, the instance of ``Foo`` may have been even serialized and passed to other systems, and it could now be running in other machines, perhaps in totally different environments and even programming languages. So these type of problems can be infectious and propagate to other parts of our systems. Tracking the source of original failure in that case could be quite tricky.

So, the key insights here are:

1. Fail as fast and as soon as possible.
2. Avoid accepting invalid data at all costs (no garbage in).
3. Above all, DTOs must be bullet proof since they traverse system boundaries and can be infectious.
4. Failure to accept invalid data not only makes your system better, it also makes better clients.

What Sort of Things Need Validation
-----------------------------------

- Nullability checks.
- Domain business rules (e.g. an order must have payments)
- Number constraints:

  * What is the valid range of values in the number? (e.g. ``1 <= hour <= 12``)
  * Can it be negative? (e.g. un-receive quantity)
  * Can it be zero? (e.g. inventory stock)
  * Can this number overflow or underflow? (e.g. ``Integer.MAX_VALUE + 1``)
  * Is the number so big that it should be a ``BigInteger`` or ``BigDecimal``?
  * If the number cannot be null, use primitive types.
  * If the number can be stored in a database field, would it fit within the size of the corresponding database field

- String constraints:

  * Does the string must satisfy a specific pattern (i.e. regex)?.
  * For other open strings, does the string have a maximum capacity?.
  * If the string is going to be stored in a given database field, does the string fits in that field?.

- Collection and arrays constraints:

  * Collections must never be null, initialize them to empty collections
  * Can the collection be empty (e.g. order items)
  * Can any of the items in the collection be null?
  * Can the collection be subject to unsafe publication?
  * Can you expose the collection only through a read-only interface like ``Iterable``, ``Iterator`` or an unmodifiable collection?

- Immutable Objects:

  * Are there any getters doing unsafe publication of mutable members?

- Mutable Objects:

  * Can any getter exposing mutable objects allow to alter the valid semantics of internal data of the mutable object?

The following quote from `Code Complete`_ highlights the main principle here:

 Check the values of all data from external sources. When getting data from a file, a user, the network, or some other external interface, check to be sure that the data falls within the allowable range. Make sure that numeric values are within tolerances and that strings are short enough to handle. If a string is intended to represent a restricted range of values (such as a financial transaction ID or something similar), be sure that the string is valid for its intended purpose; otherwise reject it. If you're working on a secure application, be especially leery of data that might attack your system: attempted buffer overflows, injected SQL commands, injected HTML or XML code, integer overflows, data passed to system calls, and so on.

 Check the values of all routine input parameters. Checking the values of routine input parameters is essentially the same as checking data that comes from an external source, except that the data comes from another routine instead of from an external interface.

Validate Public and Protected Methods
-------------------------------------

An object's public and protected methods are its way to interact with the world. From the point of view of the API designer, any parameters passed by the API user cannot be trusted since the API users could easily make a mistake or have a bug in their code. Therefore the input provided by the API users cannot be trusted and therefore all public and protected methods *must* validate their input.

The book `Effective Java`_ has a section dedicate to how to properly use exceptions (which I encourage everyone to read). The following is a valuable quote from that book:

 Use runtime exceptions to indicate programming errors. The great majority of runtime exceptions indicate precondition violations. A precondition violation is simply a failure by the client of an API to adhere to the contract established by the API specification. For example, the contract for array access specifies that the array index must be between zero and the array length minus one. ``ArrayIndexOutOfBoundsException`` indicates that this precondition was violated.

This implies validating all public and protected methods and constructors, particularly for data transport objects (i.e. DTOs).

.. code-block:: java

 public class WithdrawMoney {

    private AccountNumber accountNumber;
    private double amount;

    public WithdrawMoney(AccountNumber accountNumber, double amount) {

        Objects.requireNonNull(accountNumber, "The account number must not be null");
        if(amount <= 0) {
            throw new IllegalArgumentException("The amount must be > 0: " + amount);
        }

        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(AccountNumber accountNumber) {
        Objects.requireNonNull(accountNumber, "The account number must not be null");
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if(amount <= 0) {
            throw new IllegalArgumentException("The amount must be > 0: " + amount);
        }
        this.amount = amount;
    }
 }

Since private methods are directly accessed from public or protected methods, then there is no need to do any validation there. If all public interfaces are checked to be valid then private methods can assume any parameters passed already satisfy required preconditions.
Something similar could be said of package protected methods, since these can only be access from withing a given package, it is expected that they are under the controler of the API implementor and therefore
the implementor has much more control of whether the data is valid within the confines of that package.

The Barricade Principle
-----------------------

Once more `Code Complete`_ has great advice under Barricade Your Program to Contain the Damage Caused by Errors:

 One way to barricade for defensive programming purposes is to designate certain interfaces as boundaries to "safe" areas. Check data crossing the boundaries of a safe area for validity, and respond sensibly if the data isn't valid. Figure 8-2 illustrates this concept.

 .. image:: src/main/resources/static/images/validation-barricades.png

 This same approach can be used at the class level. The class's public methods assume the data is unsafe, and they are responsible for checking the data and sanitizing it. Once the data has been accepted by the class's public methods, the class's private methods can assume the data is safe.

 Another way of thinking about this approach is as an operating-room technique. Data is sterilized before it's allowed to enter the operating room. Anything that's in the operating room is assumed to be safe. The key design decision is deciding what to put in the operating room, what to keep out, and where to put the doors—which routines are considered to be inside the safety zone, which are outside, and which sanitize the data. The easiest way to do this is usually by sanitizing external data as it arrives, but data often needs to be sanitized at more than one level, so multiple levels of sterilization are sometimes required.

 Convert input data to the proper type at input time. Input typically arrives in the form of a string or number. Sometimes the value will map onto a boolean type like "yes" or "no." Sometimes the value will map onto a boolean type like "yes" or "no." Sometimes the value will map onto an enumerated type like ``Color_Red``, ``Color_Green``, and ``Color_Blue``. Carrying data of questionable type for any length of time in a program increases complexity and increases the chance that someone can crash your program by inputting a color like "Yes." Convert input data to the proper form as soon as possible after it's input.

The principle here is not to trust any external sources of data, and from the perspective of methods any parameters passed to public and protected methods are considered external sources of data from the perspective of the API designer vs the API implementor vs the API user. Since classes are the building blocks of our systems, making them bullet proof will ensure our systems are more robust.

The barricade principle could be implemented at different levels of abstraction. For example, by validating the input parameters of public methods we create a barricade that protects private methods within a class, making it sure for private methods to use any parameters passed to them without having to re-validate them. The barricade could also be implemented in layered by means of validating user's input in the controller layer and making sure that any user's input is sanitized by the time it reaches the service layer.


What About Dependency Injection?
--------------------------------

We can understand a few exceptions to doing input checks on parameters when it comes to arguments passed by injection of dependencies, for example

.. code-block:: java

 @Service
 public class SavingsAccountService implements BankAccountService {

    private final BankAccountRepository accountRepository;

    @Autowired
    public SavingsAccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //...
 }


In the code above I could understand an omission of a validation on the ``accountRepository`` argument, because we're using Spring to inject a value here and the ``Autowrired`` annotation already requires that a value is passed here or an exception will be thrown during the application initialization. Obviously adding a nullability check wouldn't do any harm here and I would say it is required if the class is expected to be instantiated outside the Spring container for other purposes. However, if it is intended only to be used withing the Spring container, I would omit the validation since I know the container would do the corresponding nullability checks here when it starts.

However, you may still want to validate that certain injected values are correct, particularly if they come from configuration files that can be wrongfully edited. For example:

.. code-block:: java

 @Bean
 public RetryTemplate retryTemplate(@Value("${retryAttempts}" Integer retryAttempts) {
   if(retryAttempts < 0)
      throw new IllegalArgumentException("Invalid retryAttempts configuration: " + retryAttempts);

   RetryTemplate retryTemplate = new RetryTemplate();
   SimpleRetryPolicy policy = new SimpleRetryPolicy(3, singletonMap(TransientDataAccessException.class, true), true);
   retryTemplate.setRetryPolicy(policy);

   return retryTemplate;
 }

In the example above, we know Spring guarantees the value of ``retryAttempts`` must be defined, but the value received might still be wrongfully defined in a configuration file. So an additional check here is never superfluous in my opinion.

Once more, the principle here is not to trust any external sources of data.


Strive for Immutability
-----------------------

The `benefits of immutability <http://www.yegor256.com/2014/06/09/objects-should-be-immutable.html>`_ are well known:

* Thread safety.
* Avoid temporal decoupling.
* Avoid side effects.
* Avoid identity mutability.
* Failure atomicity

A place where I believe we can always strive to use immutable objects is in our definition of our `data transfer objects <https://martinfowler.com/eaaCatalog/dataTransferObject.html>`_ (aka DTOs). Since DTOs transport data beyond the boundaries of our applications I daresay there's rarely a case in which we could say it is justifiable that we need to modify the state of such objects while using them.

.. code-block:: java

 public class SaveMoney {

    private final AccountNumber accountNumber;
    private final double amount;

    @JsonCreator
    public SaveMoney(@JsonProperty("accountNumber") AccountNumber accountNumber,
                     @JsonProperty("amount") double amount) {

        Objects.requireNonNull(accountNumber, "The account number must not be null");
        if(amount <= 0) {
            throw new IllegalArgumentException("The amount must be > 0: " + amount);
        }
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    //...
 }

Note: The annotations ``@JsonCreator``, and ``@JsonProperty`` are part of the Jackson annotations library and they are used by this library to decide how to serialize an Java object into a JSON string and deserialize it back into Java object. Since the class has not setter methods, the ``@JsonCreator`` annotation states which constructor must be used during deserialization, and ``@JsonProperty`` simply maps JSON property fields to the corresponding arguments of the constructor.

Another place where immutability can also be easily exploited is in the definition of `Value Objects <https://martinfowler.com/eaaCatalog/valueObject.html>`_. Every business domain has a set of business value objects that are highly reusable. For example, in our banking application example, instead of defining a bank account number as a String, we define a value object to represent it and encapsulate some validation with it. The advantage of value objects is that they pull their own semantic weight at the same time that they properly validate constraints over the encapsulated value. And as a bonus advantage they are highly reusable.

.. code-block:: java

 public class AccountNumber {

    //favor immutability
    private final String number;

    @JsonCreator
    public AccountNumber(String number) {
        Objects.requireNonNull(number, "The account number must not be null");
        if(!number.matches("\\d-\\d{3}-\\d{3}-\\d{3}")) {
            throw new IllegalArgumentException("Invalid savings account number format: " + number);
        }
        this.number = number;
    }

    @JsonValue
    public String getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AccountNumber that = (AccountNumber) o;

        return number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public String toString() {
        return this.number;
    }
 }

Note: the use of the ``@Json`` value annotation is fundamental here. Without it a ``AccountNumber("1-234-567-890")`` would be serialized as ``{number: "1-234-567-890"}`` instead of just ``"1-234-567-890"``. This latter is the way a value object should be serialized though.

It is fundamental that value objects have proper implementations of ``equals``, ``hashCode`` and ``toString``. For a review of how to do this the right way I'd recommend a reading of related chapters in `Effective Java`_.

Use Java 8 Optional When Possible
---------------------------------

A proper use of `Java 8 Optional <https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html>`_ or `Google Guava Optional <https://google.github.io/guava/releases/19.0/api/docs/com/google/common/base/Optional.html>`_ can alleviate a lot of mistakes related to null references. For example, in the following code the developer makes the mistake of not checking whether the reference returned by the method is null or not:

.. code-block:: java

 @Override
 public double withdrawMoney(WithdrawMoney withdrawal) {
    Objects.requireNonNull(withdrawal, "The withdrawal request must not be null");
    BankAccount account = accountRepository.findAccountByNumber(withdrawal.getAccountNumber());
    account.withdrawMoney(withdrawal.getAmount()); //Uh oh! account may be null
 }

However, if we change our repository method to return a Java 8 Optional object, it makes it harder for the developer to use the returned value without having to recognize the possibility that the optional is empty and it this case the developer does addresses the particular scenario by throwing an exception, something it was overlooked in the previous snippet.

.. code-block:: java

 @Override
 public double withdrawMoney(WithdrawMoney withdrawal) {
    Objects.requireNonNull(withdrawal, "The withdrawal request must not be null");
    return accountRepository.findAccountByNumber(withdrawal.getAccountNumber())
                            .map(account -> account.withdrawMoney(withdrawal.getAmount()))
                            .orElseThrow(() -> new BankAccountNotFoundException(withdrawal.getAccountNumber()));
 }

Beware, though, that using Optional objects improperly is also very easy. The following articles might help you avoid common pitfalls:

* `Java SE 8 Optional, a pragmatic approach <http://blog.joda.org/2015/08/java-se-8-optional-pragmatic-approach.html>`_ by Stephen Colebourne (creator of Joda Time and JDK 8 Date/Time API).
* `Should Java 8 getters return optional type? <https://stackoverflow.com/a/26328555/697630>`_ answered by Brian Goetz (lead of Java 8 project at Oracle)
* `Should I use Java8/Guava Optional for every method that may return null? <https://stackoverflow.com/a/18699418/697630>`_ which I answered myself a few years ago.

Spring Controller Barricade
---------------------------



Further Reading
---------------

* `Design By Contract`_
* `Null References: The Billion Dollar Mistake <https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare>`_
* `Objects Should Be Immutable`_
* `Data Transfer Object`_
* `Code Complete`_
* `Effective Java`_

.. _Code Complete: https://www.amazon.com/Code-Complete-Practical-Handbook-Construction/dp/0735619670
.. _Effective Java: https://www.amazon.com/Effective-Java-3rd-Joshua-Bloch/dp/0134685997/
.. _Design By Contract: https://www.cs.umd.edu/class/fall2002/cmsc214/Projects/P1/proj1.contract.html
.. _Objects Should Be Immutable: http://www.yegor256.com/2014/06/09/objects-should-be-immutable.html
.. _Data Transfer Object: https://martinfowler.com/eaaCatalog/dataTransferObject.html