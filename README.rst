Fundamental Programming Principles
==================================

.. contents:: Table of Contents
  :local:

This project explores and demonstrates fundamental programming principles in the following areas:

- Input Validation
- Proper use of exceptions
- Logging

Defensive Programming
---------------------

An excellent place to start is in Chapter 8 of the book `Code Complete`_, under Defensive Programming:

  In school, you might have heard the expression, "Garbage in, garbage out." That expression is essentially software development's version of caveat emptor: let the user beware.

  For production software, garbage in, garbage out isn't good enough. A good program never puts out garbage, regardless of what it takes in. A good program uses "garbage in, nothing out," "garbage in, error message out," or "no garbage allowed in" instead.

  By today's standards, "garbage in, garbage out" is the mark of a sloppy, nonsecure program.

Note: in Code Complete, "defensive programming" is more closer to the idea of `Design by Contract <http://se.inf.ethz.ch/~meyer/publications/computer/contract.pdf>`_ published by Bertran Meyer back in 1992, and I recommend taking time to read his paper as well.

Robustness vs. Correctness
-------------------------

There are two major principles that we must balance when designing and implementing defensive code. The following is another quote from the same book:

  Developers tend to use these terms informally, but, strictly speaking, these terms are at opposite ends of the scale from each other. *Correctness* means never returning an inaccurate result; returning no result is better than returning an inaccurate result. *Robustness* means always trying to do something that will allow the software to keep operating, even if that leads to results that are inaccurate sometimes.

  Safety-critical applications tend to favor correctness to robustness. It is better to return no result than to return a wrong result. The radiation machine is a good example of this principle.

  Consumer applications tend to favor robustness to correctness. Any result whatsoever is usually better than the software shutting down. The word processor I'm using occasionally displays a fraction of a line of text at the bottom of the screen. If it detects that condition, do I want the word processor to shut down? No. I know that the next time I hit Page Up or Page Down, the screen will refresh, and the display will be back to normal.

Balancing these two principles is vital while designing applications and coding software components.

Design By Contract
------------------

Bertran Meyer published a great article back in 1992 on the idea of `Design by Contract <http://se.inf.ethz.ch/~meyer/publications/computer/contract.pdf>`_.

Functions or methods are the fundamental building blocks of any software application, and we should strive to `design them by contract <http://wiki.c2.com/?DesignByContract>`_. This implies that a method must guarantee all its preconditions. During the execution of the code must ensure its invariants are held, and finally, when finished, it must guarantee a number of postconditions. Failure to do any of these should be signaled immediately by throwing an exception.

* **Precondition**:  a condition that must be true of the parameters of a method and/or data members, if the method is to behave correctly, prior to running the code in the method.
* **Postcondition**: a condition that is true after running the code in a method.
* **Class Invariant**: a condition that is true before and after running the code in a method (except constructors and destructors).

Therefore a method contract states what input the method receives and establishes a set of constraints or preconditions that such input must satisfy. The method contract also states what the method does. In other words what task is logically performed by the method and what results can be expected, what side effects should have happened, etc. There is also a number of postconditions that a method contract must guarantee upon successful completion of the method. In other words, guaranteed expectations about the final state of the world after the method is done running and what it should return as a result.

Any deviation of the contract must throw an exception or signal the problem somehow: make it obvious that either the preconditions were not satisfied, or the postconditions could not be met for any reason.

Failure to validate both the preconditions and postconditions of a method contract can leave a system in an inconsistent state in which the program results can no longer be guaranteed and where errors can happen unexpectedly due to the inconsistent state of the data.

A practice that we could follow is that of documenting the method contracts, explicitly stating what it does, its preconditions, invariants and postconditions. For example, consider the following code snippet from a ``BankAccount`` interface (intentionally oversimplified to make examples simpler to understand):

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
     * @param amount - the amount of money to withdraw from the account.
     * @return the balance in the account after the withdrawal.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     * @throws InsufficientFundsException if the current {@code balance} is smaller than {@code amount}
     */
    double withdrawMoney(double amount);

    /**
     * Saving money into the savings account increases its balance by the saved amount.
     *
     * In order that the saving operation succeeds the final account balance must represent a positive amount of money.
     *
     * At any point in time the final balance of the saving accounts may never be smaller than 0.
     *
     * @param amount - the amount to save into the account.
     * @return the balance of the account after savings.
     * @throws IllegalArgumentException if {@code amount} <= 0.
     */
    double saveMoney(double amount);
 }

The implementation class of this interface then must satisfy everything stated in the contract of its methods, and our test classes must strive to fulfill those contracts. Another great benefit of having these contracts stated is that just by writing them, we put ourselves in the mindset of thinking what could go wrong, which is always a good start to write defensive, robust software. Finally, once the contract is clearly stated, developers can easily write unit tests for that contract, even before the interface have been properly implemented.

Consider another example: let's say you are defining a ``Fraction`` class to represent that mathematical concept. You may need to follow a contract with the following rules:

* **Precondition**: the denominator must never be ``0``.
* **Invariant**: fractions will be kept in reduced form (i.e. ``2/3`` instead of ``6/9``, ``6`` instead of ``6/1``, ``0`` instead of ``0/2``)
* **Postcondition**: a fraction with a denominator of ``1`` will be represented as a whole number, not as a fraction (i.e. ``2`` instead of ``2/1``).
* **Postcondition**: a numerator of 0 will be represented as the whole number ``0``, not as a fraction (i.e. ``0`` instead of ``0/2``).

The **principle here** is that you may want to make the effort of documenting your interface contracts such that developers creating implementation make sure the contract holds at all times in their implementation and their unit tests.

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

If you're following TDD style, you need not have implemented the ``SavingsAccount`` class, and initially, all tests would fail and gradually start passing as the methods are implemented properly one by one in the class.

Beware of Constructor Parameters
--------------------------------

Perhaps the most classic example of validation omission is the failure to properly validate the nullability of a method argument, mainly when it happens in a constructor. For example, consider this class:

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


The problem here is that the spatial (where) and temporal (when) locations of the exception thrown here are very far away from the source of the problem (i.e., the constructor above). No wonder why Tony Hoare called his invention of null references `a billion dollars mistake <https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare>`_. However, this temporality and spatiality issue may happen with other forms of unvalidated data.

To make matters worse, in a distributed system, the instance of ``Foo`` may have been even serialized and passed to other systems, and it could now be running in other machines, perhaps in totally different environments and even programming languages. So these types of problems can be infectious and propagate to other parts of our systems. Tracking the source of the original failure, in that case, could be quite tricky.

So, the key insights here are:

1. Fail as fast and as soon as possible.
2. Avoid accepting invalid data at all costs (no garbage in).
3. Above all, DTOs must be bulletproof since they traverse system boundaries and can be infectious.
4. Failure to accept invalid data not only makes your system better, but it also makes better clients.

What Sort of Things Need Validation
-----------------------------------

- Nullability checks.
- Domain business rules (e.g., an order must have payments)
- Number constraints:

  * What is the valid range of values in the number? (e.g. ``1 <= hour <= 12``)
  * Can it be negative? (e.g., un-receive quantity)
  * Can it be zero? (e.g., inventory stock)
  * Can this number overflow or underflow? (e.g. ``Integer.MAX_VALUE + 1``)
  * Is the number so big that it should be a ``BigInteger`` or ``BigDecimal``?
  * If the number cannot be null, use primitive types.
  * If the number can be stored in a database field, would it fit within the size of the corresponding database field

- String constraints:

  * Does the string must satisfy a specific pattern (i.e., regex)?.
  * For other open strings, does the string have a maximum capacity?.
  * If the string is going to be stored in a given database field, does the string fits in that field?.

- Collection and arrays constraints:

  * Collections must never be null, initialize them to empty collections
  * Can the collection be empty (e.g., order items)
  * Can any of the items in the collection be null?
  * Can the collection be subject to unsafe publication?
  * Can you expose the collection only through a read-only interface like ``Iterable``, ``Iterator`` or an unmodifiable collection?

- Immutable Objects:

  * Are there any getters doing the unsafe publication of mutable members?

- Mutable Objects:

  * Can any getter exposing mutable objects allow to alter the valid semantics of internal data of the mutable object?

The following quote from `Code Complete`_ highlights the main principle here:

 Check the values of all data from external sources. When getting data from a file, a user, the network, or some other external interface, check to be sure that the data falls within the allowable range. Make sure that numeric values are within tolerances and that strings are short enough to handle. If a string is intended to represent a restricted range of values (such as a financial transaction ID or something similar), be sure that the string is valid for its intended purpose; otherwise, reject it. If you're working on a secure application, be especially leery of data that might attack your system: attempted buffer overflows, injected SQL commands, injected HTML or XML code, integer overflows, data passed to system calls, and so on.

 Check the values of all routine input parameters. Checking the values of routine input parameters is essentially the same as checking data that comes from an external source, except that the data comes from another routine instead of from an external interface.

I also recommend reading the `Input Validation Cheat Sheet`_.

Validate Public and Protected Methods
-------------------------------------

An object's public and protected methods are its way of interacting with the world. From the point of view of the API designer, any parameters passed by the API user cannot be trusted since the API users could easily make a mistake or have a bug in their code. Therefore the input provided by the API users cannot be trusted, and all public and protected methods *must* validate their input.

The book `Effective Java`_ has a section on how to properly use exceptions (which I encourage everyone to read). The following is a valuable quote from that book:

 Use runtime exceptions to indicate programming errors. The vast majority of runtime exceptions report precondition violations. A precondition violation is simply a failure by the client of an API to adhere to the contract established by the API specification. For example, the contract for array access specifies that the array index must be between zero and the array length minus one. ``ArrayIndexOutOfBoundsException`` indicates that this precondition was violated.

This implies validating all public and protected methods and constructors. Consider this example of data transport objects (DTO).

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

Since private methods are directly accessed from public or protected methods, then there is no need to do any validation there. If all public interfaces are checked to be valid, then private methods can assume any parameters passed to them already satisfy the required preconditions.
Something similar could be said of package protected methods, since these can only be accessed from within a given package, it is expected that they are under the control of the API implementor and therefore
the implementor has the power to determine whether the data is valid within the confines of that package.

This idea is compatible with the barricade principle.

The Barricade Principle
-----------------------

Once more `Code Complete`_ has great advice under Barricade Your Program to Contain the Damage Caused by Errors:

 One way to barricade for defensive programming purposes is to designate certain interfaces as boundaries to "safe" areas. Check data crossing the boundaries of a safe area for validity, and respond sensibly if the data isn't valid. Figure 8-2 illustrates this concept.

 .. image:: src/main/resources/static/images/validation-barricades.png

 This same approach can be used at the class level. The class's public methods assume the data is unsafe, and they are responsible for checking the data and sanitizing it. Once the data has been accepted by the class's public methods, the class's private methods can assume the data is safe.

 Another way of thinking about this approach is as an operating-room technique. Data is sterilized before it's allowed to enter the operating room. Anything that's in the operating room is assumed to be safe. The key design decision is deciding what to put in the operating room, what to keep out, and where to put the doors—which routines are considered to be inside the safety zone, which are outside, and which sanitize the data. The easiest way to do this is usually by sanitizing external data as it arrives, but data often needs to be sanitized at more than one level, so multiple levels of sterilization are sometimes required.

 Convert input data to the proper type at input time. Input typically arrives in the form of a string or number. Sometimes the value will map onto a boolean type like "yes" or "no." Sometimes the value will map onto a boolean type like "yes" or "no." Sometimes the value will map onto an enumerated type like ``Color_Red``, ``Color_Green``, and ``Color_Blue``. Carrying data of questionable type for any length of time in a program increases complexity. It increases the chance that someone can crash your program by inputting a color like "Yes." Convert input data to the proper form as soon as possible after it's input.

The principle here is not to trust any external sources of data, and from the perspective of methods any parameters passed to public, and protected methods are considered external sources of data from the perspective of the API designer vs. the API implementor vs. the API user. Since classes are the building blocks of our systems, making them bulletproof will ensure our systems are more robust.

The barricade principle could be implemented at different levels of abstraction. For example, by validating the input parameters of public methods we create a barricade that protects private methods within a class, making it sure for private methods to use any parameters passed to them without having to re-validate them. The barricade could also be implemented in layered applications by validating the user's input in the controller layer and guaranteeing that any user's input is sanitized by the time it reaches the service layer.


What About Dependency Injection?
--------------------------------

We can understand a few exceptions to doing input checks on parameters when it comes to arguments passed by injection of dependencies, for example:

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


In the code above I could understand an omission of validation on the ``accountRepository`` argument because we're using Spring to inject a value here, and the ``Autowrired`` annotation already requires that a value is passed here or an exception will be thrown during the container initialization. Obviously adding a nullability check wouldn't do any harm here, and I would say it is required if the class is expected to be instantiated outside the Spring container for other purposes. However, if it is intended only to be used within the Spring container, I might omit the validation since I know the container would do the corresponding nullability checks here when it starts.

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

A place where I believe we can always strive to use immutable objects is in the definition of our `data transfer objects <https://martinfowler.com/eaaCatalog/dataTransferObject.html>`_ (aka DTOs). Since DTOs transport data beyond the boundaries of our applications, I daresay there's rarely a case in which we could find it justifiable that we need to modify the state of such objects while using them.

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

Note: The annotations ``@JsonCreator``, and ``@JsonProperty`` are part of the Jackson annotations library, and they are used by this library to decide how to serialize a Java object into a JSON string and deserialize it back into a Java object. Since the class has no setter methods, the ``@JsonCreator`` annotation states which constructor must be used during deserialization, and ``@JsonProperty`` simply maps JSON property fields to the corresponding arguments of the constructor.

Another place where immutability can also be easily exploited is in the definition of `Value Objects <https://martinfowler.com/eaaCatalog/valueObject.html>`_. Every business domain has a set of business value objects that are highly reusable. For example, in our banking application example, instead of defining a bank account number as a String, we define a value object to represent it and encapsulate some validation with it. The advantage of value objects is that they pull their own semantic weight at the same time that they properly validate constraints over the encapsulated data. And as a bonus advantage, they are highly reusable.

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

Note: the use of the ``@JsonValue`` value annotation is fundamental here. Without it a ``AccountNumber("1-234-567-890")`` would be serialized as ``{number: "1-234-567-890"}`` instead of just ``"1-234-567-890"``. This latter is the way a value object should be serialized, though.

Fundamentally, value objects have proper implementations of ``equals``, ``hashCode`` and ``toString``. For a review of how to do this the right way I'd recommend a reading of related chapters in `Effective Java`_. Alternatively, to make you life simpler, consider using Apache Commons `EqualsBuilder <https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/builder/EqualsBuilder.html>`_, `HashCodeBuilder <https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/builder/HashCodeBuilder.html>`_ and `ToStringBuilder <https://commons.apache.org/proper/commons-lang/javadocs/api-release/org/apache/commons/lang3/builder/ToStringBuilder.html>`_.

Use Java 8 Optional When Possible
---------------------------------

A proper use of `Java 8 Optional <https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html>`_ or `Google Guava Optional <https://google.github.io/guava/releases/19.0/api/docs/com/google/common/base/Optional.html>`_ can alleviate a lot of mistakes related to null references. For example, in the following code the developer makes the mistake of not checking whether the reference returned by ``accountRepository.findAccountByNumber`` is null or not:

.. code-block:: java

 @Override
 public double withdrawMoney(WithdrawMoney withdrawal) {
    Objects.requireNonNull(withdrawal, "The withdrawal request must not be null");
    BankAccount account = accountRepository.findAccountByNumber(withdrawal.getAccountNumber());
    account.withdrawMoney(withdrawal.getAmount()); //Uh oh! account may be null
 }

However, if we change our repository method to return an ``Optional`` object, it makes it harder for the developer to use the returned value without having to recognize the possibility that the optional might be empty and, it this case, force the developer to address that particular scenario by throwing an exception. Something that was overlooked in the previous snippet.

.. code-block:: java

 @Override
 public double withdrawMoney(WithdrawMoney withdrawal) {
    Objects.requireNonNull(withdrawal, "The withdrawal request must not be null");
    return accountRepository.findAccountByNumber(withdrawal.getAccountNumber())
                            .map(account -> account.withdrawMoney(withdrawal.getAmount()))
                            .orElseThrow(() -> new BankAccountNotFoundException(withdrawal.getAccountNumber()));
 }

Quoting Google `Guava's article <https://github.com/google/guava/wiki/UsingAndAvoidingNullExplained#whats-the-point>`_ about the use of optional objects:

 Besides the increase in readability that comes from giving null a name, the biggest advantage of Optional is its idiot-proof-ness. It forces you to actively think about the absent case if you want your program to compile at all since you have to actively unwrap the Optional and address that case.

Beware, though, that using Optional objects improperly is also very easy. The following articles might help you avoid common pitfalls:

* `Java SE 8 Optional, a pragmatic approach <http://blog.joda.org/2015/08/java-se-8-optional-pragmatic-approach.html>`_ by Stephen Colebourne (creator of Joda Time and JDK 8 Date/Time API).
* `Should Java 8 getters return optional type? <https://stackoverflow.com/a/26328555/697630>`_ answered by Brian Goetz (lead of Java 8 project at Oracle)
* `Should I use Java8/Guava Optional for every method that may return null? <https://stackoverflow.com/a/18699418/697630>`_ which I answered myself a few years ago.
* `Effective Java`_, Item 55: Return Optionals Judiciously.

Spring Controller Barricade
---------------------------

Following the barricade principle mentioned above, in a layered application, we will probably want to place that barricade in the controller layer, which is the place where we receive the user's input for a given operation. Basically, we want to avoid that the user's input goes beyond the controller if it is invalid. If a given transport object reaches the service layer, it is because it has been properly validated.

Consider the following example:

.. code-block:: java

 @RestController
 @RequestMapping("/accounts")
 public class SavingsAccountController {

    private final BankAccountService accountService;

    @Autowired
    public SavingsAccountController(SavingsAccountService accountService) {
        this.accountService = accountService;
    }

    @PutMapping("withdraw")
    public ResponseEntity<AccountBalance> onMoneyWithdrawal(@RequestBody @Validated WithdrawMoney withdrawal, BindingResult errors) {

        //this is the validation barrier
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        //any exception thrown here will be handled in the ExceptionHandlers class
        double balance = accountService.withdrawMoney(withdrawal);
        return ResponseEntity.ok(new AccountBalance(
                withdrawal.getAccountNumber(), balance));
    }

    @PutMapping("save")
    public ResponseEntity<AccountBalance> onMoneySaving(@RequestBody @Validated SaveMoney savings, BindingResult errors) {

        //this is the validation barrier
        if (errors.hasErrors()) {
            throw new ValidationException(errors);
        }

        //any exception thrown here will be handled in the ExceptionHandlers class
        double balance = accountService.saveMoney(savings);
        return ResponseEntity.ok(new AccountBalance(
                savings.getAccountNumber(), balance));
    }
 }

In the code above, we're using `Bean Validation`_ to check that the user's DTO contains valid information. Any errors found in the DTO are provided through the ``BindingResult errors`` variable, from where the developer can extract all the details of what went wrong during the validation phase. It is very clear from the code above that if any validation errors are found, we'll never reach the service layer. This is where barrier is located.

To make it easier for the developers to deal with this pattern, in the code above, I simply wrap the ``BindingResult`` into a custom ``ValidationException`` which knows how to extract the validation error details.

.. code-block:: java

 public class ValidationException extends RuntimeException {

    private final BindingResult errors;

    public ValidationException(BindingResult errors) {
        this.errors = errors;
    }

    public List<String> getMessages() {
        return getValidationMessage(this.errors);
    }


    @Override
    public String getMessage() {
        return this.getMessages().toString();
    }


    //demonstrate how to extract a message from the binging result
    private static List<String> getValidationMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors()
                .stream()
                .map(ValidationException::getValidationMessage)
                .collect(Collectors.toList());
    }

    private static String getValidationMessage(ObjectError error) {
        if (error instanceof FieldError) {
            FieldError fieldError = (FieldError) error;
            String className = fieldError.getObjectName();
            String property = fieldError.getField();
            Object invalidValue = fieldError.getRejectedValue();
            String message = fieldError.getDefaultMessage();
            return String.format("%s.%s %s, but it was %s", className, property, message, invalidValue);
        }
        return String.format("%s: %s", error.getObjectName(), error.getDefaultMessage());
    }

 }

Exception Serialization
-----------------------

How should the controller layer deal with the exceptions? In the code above the ``ValidationException`` will be thrown when the payload is invalid. How should the controller create a response for the client out of this?

There are multiple ways to deal with this, but perhaps the simplest solution is to define a class annotated as ``@ControllerAdvice``. In this annotated class we will place our exception handlers for any specific exception that we want to handle and turn them into a valid response object to travel back to our clients:

.. code-block:: java

 @ControllerAdvice
 public class ExceptionHandlers {

    @ExceptionHandler
    public ResponseEntity<ErrorModel> handle(ValidationException ex) {
        return ResponseEntity.badRequest()
                             .body(new ErrorModel(ex.getMessages()));
    }

    //...
 }

Since we are not using Java RMI as the serialization protocol for our services, we simply cannot send a Java ``Exception`` object back to the client. Instead, we must inspect the exception object generated by our application and construct a valid, serializable transport object that we can indeed send back to our clients. For that matter, we defined an ``ErrorModel`` transport object, and we simply populated it with details from the exception in their corresponding handler. This is a simplified version of what could be done. Perhaps for real production applications we may want to put a few more details in this error model (e.g., status codes, reason codes, etc.).

.. code-block:: java

 /**
  * Data Transport Object to represent errors in JSON
  */
 public class ErrorModel {

    private final List<String> messages;

    @JsonCreator
    public ErrorModel(@JsonProperty("messages") List<String> messages) {
        this.messages = messages;
    }

    public ErrorModel(String message) {
        this.messages = Collections.singletonList(message);
    }

    public List<String> getMessages() {
        return messages;
    }
 }

Finally, notice how the error handler code from the ``ExceptionHandlers`` from before treats any ``ValidationException`` as HTTP Status 400: Bad Request. That will allow the client to inspect the status code of the response and discover that our service rejected their payload because there is something wrong with it.


Design Contextual Exceptions
----------------------------

The principles here are:

* Good exceptions contain all the relevant details of their context such that any catching blocks can get any necessary information to handle them.
* Strive to design exceptions specific to your business operations. Exceptions that already convey business semantics. This is better than just throwing ``RuntimeException`` or any other generic exception.
* Design your exceptions to log all this meaningful information beautifully.

So, the first point here is that designing good exceptions implies that the exceptions should encapsulate any contextual details from the place where the exception is being thrown. This information can be vital for a catching block to handle the exception or it can be very useful during troubleshooting to determine the exact state of the system when the problem occurred, making it easier for the developers to reproduce the exact same event.

Additionally, it is ideal that exceptions themselves convey some business semantics. In other words, instead of just throwing ``RuntimeException`` it is better if we create an exception that already conveys semantics of the specific condition under which it occurred.

Consider the following example:

.. code-block:: java

  public class SavingsAccount implements BankAccount {

     //...

     @Override
     public double withdrawMoney(double amount) {
         if(amount <= 0)
             throw new IllegalArgumentException("The amount must be >= 0: " + amount);

         if(balance < amount) {
             throw new InsufficientFundsException(accountNumber, balance, amount);
         }
         balance -= amount;

         return balance;
     }

     //...

  }


Notice in the example above how we have defined a semantic exception ``InsufficientFundsException`` to represent the exceptional condition of not having sufficient funds in an account when somebody tries to withdraw an invalid amount of money from it. This is a specific business exception.

Also, notice how the exception carries all the contextual details of why this is considered an exceptional condition: it encapsulates the account number affected, its current balance, and the amount of money we were trying to withdraw when the exception was thrown.

Any block catching this exception has sufficient details to determine what happened (since the exception itself is semantically meaningful) and why it happened (since the contextual details encapsulated within the exception object contain that information).

The definition of our exception class could be somewhat like this:

.. code-block:: java

 /**
  * Thrown when the bank account does not have sufficient funds to satisfy
  * an operation, e.g. a withdrawal.
  */
 public class InsufficientFundsException extends SavingsAccountException {

    private final double balance;
    private final double withdrawal;

    //stores contextual details
    public InsufficientFundsException(AccountNumber accountNumber, double balance, double withdrawal) {
        super(accountNumber);
        this.balance = balance;
        this.withdrawal = withdrawal;
    }

    public double getBalance() {
        return balance;
    }

    public double getWithdrawal() {
        return withdrawal;
    }

    //the importance of overriding getMessage to provide a personalized message
    @Override
    public String getMessage() {
        return String.format("Insufficient funds in bank account %s: (balance $%.2f, withdrawal: $%.2f)." +
                                     " The account is short $%.2f",
                this.getAccountNumber(), this.balance, this.withdrawal, this.withdrawal - this.balance);
    }
 }

This strategy makes it possible that if, at any point, an API user wants to catch this exception to handle it in any way, that API user can gain access to the specific details of why this exception occurred, even if the original parameters (passed to the method where the exception occurred) are no longer available in the context where the exception is being handled.

One of such places where we'll want to handle this exception in our ``ExceptionHandlers`` class from before. In the code below notice how the exception is handled in a place where it is totally out of context from the place where it was thrown. Still, since the exception contains all contextual details, we are capable of building a very meaningful, contextual message to send back to our API client.

.. code-block:: java

 @ControllerAdvice
 public class ExceptionHandlers {

    //...

    @ExceptionHandler
    public ResponseEntity<ErrorModel> handle(InsufficientFundsException ex) {

        //look how powerful are the contextual exceptions!!!
        String message = String.format("The bank account %s has a balance of $%.2f. Therefore you cannot withdraw $%.2f since you're short $%.2f",
                ex.getAccountNumber(), ex.getBalance(), ex.getWithdrawal(), ex.getWithdrawal() - ex.getBalance());

        logger.warn(message, ex);
        return ResponseEntity.badRequest()
                             .body(new ErrorModel(message));
    }

    //...
 }

Also, it also worth noticing that the ``getMessage()`` method of ``InsufficientFundsException`` was overridden in this implementation. The contents of this message is what our log stack traces will display if we decide to log this particular exception. Therefore it is of paramount importance that we always override this method in our exceptions classes such that those valuable contextual details they contain are also rendered in our logs. It is in those logs where those details will most likely make a difference when we are trying to diagnose a problem with our system:

::

 com.training.validation.demo.api.InsufficientFundsException: Insufficient funds in bank account 1-234-567-890: (balance $0.00, withdrawal: $1.00). The account is short $1.00
    at com.training.validation.demo.domain.SavingsAccount.withdrawMoney(SavingsAccount.java:40) ~[classes/:na]
    at com.training.validation.demo.impl.SavingsAccountService.lambda$null$0(SavingsAccountService.java:45) ~[classes/:na]
    at java.util.Optional.map(Optional.java:215) ~[na:1.8.0_141]
    at com.training.validation.demo.impl.SavingsAccountService.lambda$withdrawMoney$2(SavingsAccountService.java:45) ~[classes/:na]
    at org.springframework.retry.support.RetryTemplate.doExecute(RetryTemplate.java:287) ~[spring-retry-1.2.1.RELEASE.jar:na]
    at org.springframework.retry.support.RetryTemplate.execute(RetryTemplate.java:164) ~[spring-retry-1.2.1.RELEASE.jar:na]
    at com.training.validation.demo.impl.SavingsAccountService.withdrawMoney(SavingsAccountService.java:40) ~[classes/:na]
    at com.training.validation.demo.controllers.SavingsAccountController.onMoneyWithdrawal(SavingsAccountController.java:35) ~[classes/:na]

Exception Chaining and Leaky Abstractions
-----------------------------------------

The principles here are:

* Developers must know very well the abstractions they are using and be aware of any exceptions these abstractions or classes may throw.
* Exceptions from your libraries should not be allowed to escape from within your own abstractions.
* Make sure to use exception chaining to avoid that important contextual details are lost when you wrap low-level exceptions into higher-level exceptions.

Effective Java explains it very well:

 It is disconcerting when a method throws an exception that has no apparent connection to the task that it performs. This often happens when a method propagates an exception thrown by a lower-level abstraction. Not only is it disconcerting, but it pollutes the API of the higher layer with implementation details. If the implementation of the higher layer changes in a later release, the exceptions it throws will change too, potentially breaking existing client programs.

 To avoid this problem, higher layers should catch lower-level exceptions and, in their place, throw exceptions that can be explained in terms of the higher-level abstraction. This idiom is known as exception translation:

.. code-block:: java

   // Exception Translation
   try {
      //Use lower-level abstraction to do our bidding
      //...
   } catch (LowerLevelException cause) {
      throw new HigherLevelException(cause, context, ...);
   }

Every time we use a third-party API, library or framework, our code is subject to fail for exceptions being thrown by their classes. We simply must not allow that those exceptions escape from our abstractions. Exceptions being thrown by the libraries we use should be translated to appropriate exceptions from our own API exception hierarchy.

For example, for your data access layer, you should avoid leaking exceptions like ``SQLException`` or ``IOException`` or ``JPAException``. Instead, you may want to define a hierarchy of valid exceptions for you API. You may define a super class exception from which your specific business exceptions can inherit from and use that exception as part of your contract.

Consider the following example from our ``SavingsAccountService``:

.. code-block:: java

 @Override
 public double saveMoney(SaveMoney savings) {

    Objects.requireNonNull(savings, "The savings request must not be null");

    try {
        return accountRepository.findAccountByNumber(savings.getAccountNumber())
                                .map(account -> account.saveMoney(savings.getAmount()))
                                .orElseThrow(() -> new BankAccountNotFoundException(savings.getAccountNumber()));
    }
    catch (DataAccessException cause) {
        //avoid leaky abstractions and wrap lower level abstraction exceptions into your own exception
        //make sure you keep the exception chain intact such that you don't lose sight of the root cause
        throw new SavingsAccountException(savings.getAccountNumber(), cause);
    }
 }

In the example above, we recognize that our data access layer might fail in recovering the details of our savings account. There is no certainty of how this might fail, however, we know that the Spring framework has a root exception for all data access exceptions: ``DataAccessException``. In this case, we catch any possible data access failures and wrap them into a ``SavingsAccountException`` to avoid that the underlying abstraction exceptions escape our own abstraction.

It is worth noticing how the ``SavingsAccountException`` not only provides contextual details, but also wraps the underlying exception. This exception chaining is a fundamental piece of information that is included in the stack trace when the exception is logged. Without these details we could only know that our system failed, but not why:

::

 com.training.validation.demo.api.SavingsAccountException: Failure to execute operation on account '1-234-567-890'
    at com.training.validation.demo.impl.SavingsAccountService.lambda$withdrawMoney$2(SavingsAccountService.java:51) ~[classes/:na]
    at org.springframework.retry.support.RetryTemplate.doExecute(RetryTemplate.java:287) ~[spring-retry-1.2.1.RELEASE.jar:na]
    at org.springframework.retry.support.RetryTemplate.execute(RetryTemplate.java:164) ~[spring-retry-1.2.1.RELEASE.jar:na]
    at com.training.validation.demo.impl.SavingsAccountService.withdrawMoney(SavingsAccountService.java:40) ~[classes/:na]
    at com.training.validation.demo.controllers.SavingsAccountController.onMoneyWithdrawal(SavingsAccountController.java:35) ~[classes/:na]
    at java.lang.Thread.run(Thread.java:748) [na:1.8.0_141]
    ... 38 common frames omitted
 Caused by: org.springframework.dao.QueryTimeoutException: Database query timed out!
    at com.training.validation.demo.impl.SavingsAccountRepository.findAccountByNumber(SavingsAccountRepository.java:31) ~[classes/:na]
    at com.training.validation.demo.impl.SavingsAccountRepository$$FastClassBySpringCGLIB$$d53e9d8f.invoke(<generated>) ~[classes/:na]
    ... 58 common frames omitted

The ``SavingsAccountException`` is a somewhat generic exception for our savings account services. Its semantic power is a bit limited, though. For example, it tells us there was a problem with a savings account, but it does not explicitly tell us what exactly. For that matter, we may consider adding an additional message or weight the possibility of defining a more contextual exception (e.g., ``WithdrawMoneyException``).
Given its generic nature, it could be used as the root of our hierarchy of exceptions for our savings account services.

.. code-block:: java

 /**
  * Thrown when any unexpected error occurs during a bank account transaction.
  */
 public class SavingsAccountException extends RuntimeException {

    //all SavingsAccountException are characterized by the account number.
    private final AccountNumber accountNumber;

    public SavingsAccountException(AccountNumber accountNumber) {
        this.accountNumber = accountNumber;
    }

    public SavingsAccountException(AccountNumber accountNumber, Throwable cause) {
        super(cause);
        this.accountNumber = accountNumber;
    }

    public SavingsAccountException(String message, AccountNumber accountNumber, Throwable cause) {
        super(message, cause);
        this.accountNumber = accountNumber;
    }

    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    //the importance of overriding getMessage
    @Override
    public String getMessage() {
        return String.format("Failure to execute operation on account '%s'", accountNumber);
    }
 }

Checked vs. Unchecked Exceptions
-------------------------------

Java is one of those few languages that support this feature of checked exceptions, and there's a lot of controversy on whether this was a good idea or not. Consider reading the following articles:

* `Trouble with Checked Exceptions`_
* `The Exceptions Debate`_
* `Does Java Need Checked Exceptions?`_

Even Java Specifications tend to get divided in this arena, for example, JDBC API uses the checked exception ``SQLException`` in most of their interface methods. However, the JPA specification, which is also about data access, uses ``JPAException`` for everything, and this one is an unchecked exception.

In `Effective Java`_ we read the following advice about checked exceptions:

 The cardinal rule in deciding whether to use a checked or an unchecked exception is this: use checked exceptions for conditions from which the caller can reasonably be expected to recover. By throwing a checked exception, you force the caller to handle the exception in a catch clause or to propagate it outward. Each checked exception that a method is declared to throw is, therefore, a potent indication to the API user that the associated condition is a possible outcome of invoking the method.

Regardless of the opinion we have on checked vs unchecked exceptions the main issue you definitively will want to consider with checked exceptions is that they don't play well with Java 8 functional interfaces, making them really hard to use with any methods that throw them (e.g. in fluent code of Stream API or reactive programming libraries like `RxJava <https://github.com/ReactiveX/RxJava>`_ or `Reactor <https://projectreactor.io>`_).

The migration of applications using checked exceptions in Java 6 o 7 into Java 8 applications using lambdas, method references, and stream API could quickly become a nightmare of super verbosity.

Since checked exceptions are part of the method signature, methods throwing checked exceptions are incompatible with most of the Java 8 functional interfaces or other with third-party API functional interfaces.

If you are interested in knowing more, in the past I had answered a question in Stackoverflow explaining this and `several other shortcomings in the Java type system <https://stackoverflow.com/a/22919112/697630>`_ that would make developers lives much harder if they had to deal with checked exceptions every time they need to use them in lambda expression.

The principle here is to avoid checked exceptions and favor unchecked exceptions when possible.

Retryability: Transient vs Persistent Exceptions
------------------------------------------------

Some exceptions represent recoverable conditions (e.g. a ``QueryTimeoutException``) and some don't (e.g. ``DataViolationException``).

When an exception condition is temporal, and we believe that if we try again, we could probably succeed, we say that such exception is transient. On the other hand, when the exceptional condition is permanent, then we say such exception is persistent.

The major point here is that transient exceptions are good candidates for retry blocks, whereas persistent exceptions need to be handled differently, typically requiring some human intervention.

This knowledge of the 'transientness' of exceptions becomes even more relevant in distributed systems where an exception can be serialized somehow and sent beyond the boundaries of the system. For example, if the client API receives an error reporting that a given HTTP endpoint failed to execute, how can the client know if the operation should be retried or not? It would be pointless to retry if the condition for which it failed was permanent.

When we design an exception hierarchy based on a good understanding of the business domain and the classical system integration problems, then the information of whether an exception represents a recoverable condition or not can be crucial to design right behaving clients.

There are several strategies we could follow to indicate an exception is transient or not within our APIs:

* We could document that a given exception is transient (e.g., JavaDocs).
* We could define a ``@TransientException`` annotation and add it to the exceptions.
* We could define a marker interface or inherit from a ``TransientServiceException`` class.

The Spring Framework follows the approach in the third option for its data access classes. All exceptions that inherit from `TransientDataAccessException`_ are considered transient and retryable in Spring.

This plays rather well with the `Spring Retry`_ Framework. It becomes particularly simple to define a retry policy that retries any operation that caused a transient exception in the data access layer. Consider the following example:

.. code-block:: java

  @Override
  public double withdrawMoney(WithdrawMoney withdrawal) throws InsufficientFundsException {
     Objects.requireNonNull(withdrawal, "The withdrawal request must not be null");

     //we may also configure this as a bean
     RetryTemplate retryTemplate = new RetryTemplate();
     SimpleRetryPolicy policy = new SimpleRetryPolicy(3, singletonMap(TransientDataAccessException.class, true), true);
     retryTemplate.setRetryPolicy(policy);

     //dealing with transient exceptions locally by retrying up to 3 times
     return retryTemplate.execute(context -> {
         try {
             return accountRepository.findAccountByNumber(withdrawal.getAccountNumber())
                                     .map(account -> account.withdrawMoney(withdrawal.getAmount()))
                                     .orElseThrow(() -> new BankAccountNotFoundException(withdrawal.getAccountNumber()));
         }
         catch (DataAccessException cause) {
            //we get here only for persistent exceptions
            //or if we exhausted the 3 retry attempts of any transient exception.
            throw new SavingsAccountException(withdrawal.getAccountNumber(), cause);
         }
     });
  }

In the code above, if the DAO fails to retrieve a record from the database due to e.g., a query timeout, Spring would wrap that failure into a `QueryTimeoutException`_ which is also a `TransientDataAccessException`_ and our ``RetryTemplate`` would retry that operation up to 3 times before it surrenders.

**How about transient error models?**

When we send error models back to our clients, we can also take advantage of knowing if a given exception is transient or not. This information let us tell the clients that they could retry the operation after a certain back off period.

.. code-block:: java

  @ControllerAdvice
  public class ExceptionHandlers {

    private final BinaryExceptionClassifier transientClassifier = new BinaryExceptionClassifier(singletonMap(TransientDataAccessException.class, true), false);
    {
        transientClassifier.setTraverseCauses(true);
    }

    //..

    @ExceptionHandler
    public ResponseEntity<ErrorModel> handle(SavingsAccountException ex) {
        if(isTransient(ex)) {
            //when transient, status code 503: Service Unavailable is sent
            //and a backoff retry period of 5 seconds is suggested to the client
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                 .header("Retry-After", "5000")
                                 .body(new ErrorModel(ex.getMessage()));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ErrorModel(ex.getMessage()));
        }
    }

    private boolean isTransient(Throwable cause) {
        return transientClassifier.classify(cause);
    }

 }

The code above uses a ``BinaryExceptionClassifier``, which is part of the `Spring Retry`_ library, to determine if a given exception contains any transient exceptions in their causes and, if so, categorizes that exception as transient. This predicate is used to determine what type of HTTP status code we send back to the client. If the exception is transient, we send a ``503 Service Unavailable`` and provide a header ``Retry-After: 5000`` with the details of the backoff policy.

Using this information, clients can decide whether it makes sense to retry a given web service invocation and exactly how long they need to wait before retrying.

Logging with Monitoring in Mind
-------------------------------

All these efforts we have put in writing defensive code and designing and implementing good exceptions pay off when we also add another principle to the mix:

Design your applications with monitoring in mind.

And the most fundamental tool we have at our disposal is logging. We must strive to log everything relevant that occurs in our application, and that could help us troubleshoot any issues when the application is failing in production, and we cannot easily debug the code step by step.

* Log any errors that occur with their full stack traces. Just be sensitive that not all errors are critical (e.g., transient exceptions might be logged as warnings).
* Make sure your logs always contain contextual details, particularly strive for keeping a correlation id that helps you keep track of related long entries (e.g. all entries affecting the same bank account should have such bank account number logged).
* You may want to log when successful operations finished successfully.

Successes can be logged exactly where they occur:

.. code-block:: java

 @Override
 public double withdrawMoney(double amount) {
    if(amount <= 0)
        throw new IllegalArgumentException("The amount must be >= 0: " + amount);

    if(balance < amount) {
        throw new InsufficientFundsException(accountNumber, balance, amount);
    }
    balance -= amount;

    logger.info("Withdrew ${} from account {} for a final balance of ${}", amount, accountNumber, balance);

    return balance;
 }

And we could deal with logging errors in our ``ExceptionHandlers`` class:


.. code-block:: java

 @ExceptionHandler
 public ResponseEntity<ErrorModel> handle(SavingsAccountException ex) {
    if(isTransient(ex)) {
        //notice how logging level changes depending on whether the exception is transient or persistent
        logger.warn("Failure while processing operation on savings account: {}", ex.getAccountNumber(), ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                             .header("Retry-After", "5000")
                             .body(new ErrorModel(ex.getMessage()));
    } else {
        logger.error("Failure while processing operation on savings account: {}", ex.getAccountNumber(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ErrorModel(ex.getMessage()));
    }
 }

Notice that in all logging examples from above, the account number is always present in the log entry, one way or another. This will make it possible for the developers to easily search the logs for specific entries of a given bank account and discover everything that happened to it.

Bean Validation Drawbacks
-------------------------

When we use `Bean Validation`_ there is this expectation that we can create an object that may be initially defined in an inconsistent or invalid state, and then later, we run a validation API on it to discover whether the object violates any constraints.

.. code-block:: java

 public class SaveMoney {

    private AccountNumber accountNumber;
    private double amount;

    @NotNull
    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(AccountNumber accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Min(1)
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
 }

As you can see, just by invoking the ``new SaveMoney()`` constructor we end up with an instance of this object in a completely invalid state (i.e. account number is null, and the amount is 0.0). It setter methods are not better; we could also use them to put the object in an invalid state:

.. code-block:: java

  SaveMoney savings = new SaveMoney(); //instance is already invalid with a null account and amount of 0.0
  savings.setAccount(null);
  savings.setAmount(-1.0);

  //we have to resort to a third-party api to validate our object
  ValidatorFactory vf = Validation.buildDefaultValidatorFactory();
  Validator validator = vf.getValidator();

  Set<ConstraintViolation<SaveMoney>> violations;
  violations = validator.validate(savings);
  if(violations.size() > 0){
      throw new ValidationException(violations);
  }

For me, this possibility of having an object in an inconsistent state is a major design flaw. My point is that if the object was properly designed, it should defend itself from getting into an invalid state since its inception.

We could improve things a little bit if we made our setter methods also to do validations:

.. code-block:: java

 public void setAccountNumber(AccountNumber accountNumber) {
    Objects.requireNonNull(accountNumber, "The account number must not be null");
    this.accountNumber = accountNumber;
 }

 public void setAmount(double amount) {
    if(amount <= 0) {
        throw new IllegalArgumentException("The amount must be > 0: " + amount);
    }
    this.amount = amount;
 }

However, if we just do this, we should also include a constructor, otherwise, the object may still be built in an invalid state:

.. code-block:: java

 public class SaveMoney {

    private AccountNumber accountNumber;
    private double amount;

    public SaveMoney(AccountNumber accountNumber, double amount) {
        Objects.requireNonNull(accountNumber, "The account number must not be null");
        if(amount <= 0) {
            throw new IllegalArgumentException("The amount must be > 0: " + amount);
        }
        this.accountNumber = accountNumber;
        this.amount = amount;
    }

    @NotNull
    public AccountNumber getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(AccountNumber accountNumber) {
        this.accountNumber = accountNumber;
    }

    @Min(1)
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

At this point, the object is self-defensive. It cannot be built in an inconsistent state. But once you realize that this is the case, then **what do we need bean validation for?**. If the object guarantees it is always in a consistent state there is no need to validate it any further.

Even more, in a case like this you can probably get rid of the setter methods and make your object entirely immutable and just survive with the validations in the constructor, which make things even simpler: no bean validation whatsoever.

.. code-block:: java

 public class SaveMoney {

    //strive to design immutable DTOs
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
 }

**How do we build the controller barrier then?**

The thing is that if an API user sends an invalid JSON object, the deserialization of that object will fail when invoking our ``SaveMoney`` constructor. Consider the following example:

.. code-block:: java

 public static void main(String[] args) throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    String json = "{\"account\": null, \"amount\": -1.0}";
    SaveMoney savings = mapper.readValue(json, SaveMoney.class);
 }

Our ``mapper.readValue`` method above fails to deserialize our JSON object because the account is null. The failure is expected in our defensive constructor.

::

 Exception in thread "main" com.fasterxml.jackson.databind.JsonMappingException: Can not construct instance of com.training.validation.demo.transports.SaveMoney, problem: The account number must not be null
  at [Source: {"account": null, "amount": -1.0}; line: 1, column: 33]
    at com.fasterxml.jackson.databind.JsonMappingException.from(JsonMappingException.java:277)
 Caused by: java.lang.NullPointerException: The account number must not be null
    at java.util.Objects.requireNonNull(Objects.java:228)
    at com.training.validation.demo.transports.SaveMoney.<init>(SaveMoney.java:26)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
    at com.fasterxml.jackson.databind.introspect.AnnotatedConstructor.call(AnnotatedConstructor.java:124)
    at com.fasterxml.jackson.databind.deser.std.StdValueInstantiator.createFromObjectWith(StdValueInstantiator.java:274)
    ... 14 more

So, the first change we must do is to change the way we build our validation barrier in the controller. We no longer need to use bean validation or ``BindingResult`` objects since our immutable objects already guarantee that if it reaches the controller layer, then it is completely valid. If it is invalid, it will fail in the deserialization phase, though.

.. code-block:: java

 @PutMapping("save")
 public ResponseEntity<AccountBalance> onMoneySaving(@RequestBody SaveMoney savings) {
    double balance = accountService.saveMoney(savings);
    return ResponseEntity.ok(new AccountBalance(
            savings.getAccountNumber(), balance));
 }

To deal with the possibility of a deserialization failure of our now self-defensive object we must improve our ``ExceptionHandlers`` class to deal with any validation failures we may encounter:

.. code-block:: java

 @ControllerAdvice
 public class ExceptionHandlers extends ResponseEntityExceptionHandler {

    //...

    //since we add nullability and constraints checks to our DTOs in their constructors
    //these might fail even before reaching the Bean Validation phase, so by adding this
    //handler we make sure to respond with an appropriate error model when that occurs.

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Throwable cause = ex.getCause();
        while (cause != null && !(cause instanceof NullPointerException || cause instanceof IllegalArgumentException)) {
            cause = cause.getCause();
        }
        if (cause != null) {
            return ResponseEntity.badRequest()
                                 .body(new ErrorModel(singletonList(cause.getMessage())));
        }
        return super.handleHttpMessageNotReadable(ex, headers, status, request);
    }

    //...

 }

Notice how our ``ExceptionHandlers`` class now extends ``ResponseEntityExceptionHandler`` and we override the ``handleHttpMessageNotReadable`` method for the particular case of a ``HttpMessageNotReadableException``, which is the exception Spring throws when it fails to deserialize our JSON object.

In the handler, we go over the tree of causes of the exception to determine if the original cause was ``NullPointerException`` or a ``IllegalArgumentException`` which are the two exceptions we use to validate our DTOs. If so, we handle the case by sending a 400 Bad Request with the corresponding ``ErrorModel`` object containing the same details given in the exception message. The net effect is similar to what bean validation would have sent.

In general, I tend to prefer this approach better than using bean validation. Its major advantages are that the objects are always consistent and valid, and I can exploit immutability. Its main disadvantage (compared to bean validation) is that it only reports one constraint at the time.

Testing Components
------------------

In the following section, I present some ideas of different aspects that are worth testing for every type of component.

Transport and Value Objects
^^^^^^^^^^^^^^^^^^^^^^^^^^^

For **immutable transport objects** and **value object**, I recommend **at least** testing the preconditions of the constructor, the validity of the equals and hascCode implementations and that serialization/deserialization works properly.

.. code-block:: java

 public class AccountNumberTest {

     @Test
     public void testValidConstruction() {
         AccountNumber accountNumber = new AccountNumber("1-234-567-890");
         assertThat(accountNumber.getNumber()).isEqualTo("1-234-567-890");
         assertThat(accountNumber.toString()).isEqualTo("1-234-567-890");
     }


     @Test(expected = NullPointerException.class)
     public void testInvalidAccountConstruction() {
         new AccountNumber(null);
         fail("The AccountNumber object must not be created with an invalid account number!");
     }

     @Test
     public void testEqualityContract() {

         AccountNumber alpha = new AccountNumber("1-234-567-890");
         AccountNumber beta = new AccountNumber("1-234-567-890");
         AccountNumber gamma = new AccountNumber("1-234-567-890");
         AccountNumber delta = new AccountNumber("9-876-543-210");

         //reflexive quality
         assertTrue(alpha.equals(alpha));

         //reflexive quality
         assertTrue(alpha.equals(beta));
         assertTrue(beta.equals(alpha));

         //transitive quality
         assertTrue(beta.equals(gamma));
         assertTrue(alpha.equals(gamma));

         //inequality
         assertFalse(alpha.equals(delta));

         //hashcode consistency
         assertTrue(alpha.hashCode() == beta.hashCode());
     }

     @Test
     public void testSerialization() {

         ObjectMapper mapper = new ObjectMapper();
         try {
             AccountNumber source = new AccountNumber("1-234-567-890");
             String json = mapper.writeValueAsString(source);
             AccountNumber copy = mapper.readValue(json, AccountNumber.class);
             assertThat(source).isEqualTo(copy);
         }
         catch (Exception e) {
             fail(e.getMessage());
         }
     }
 }

The Service Layer
^^^^^^^^^^^^^^^^^

For your **service layer**, you may want to use a library like Mockito to mock your data access layer and just focus on what should happen in the service layer. Make sure to test not only valid scenarios but also invalid scenarios and attempts to violate preconditions.

.. code-block:: java

 @RunWith(MockitoJUnitRunner.class)
 public class SavingsAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private SavingsAccountService savingsAccountService;

    private final AccountNumber accountNumber = new AccountNumber("1-234-567-890");

    @Test
    public void testSuccessfulMoneySaving() {
        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.of(new SavingsAccount(accountNumber)));

        SaveMoney savings = new SaveMoney(new AccountNumber("1-234-567-890"), 100);
        double balance = savingsAccountService.saveMoney(savings);

        verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
        verifyNoMoreInteractions(bankAccountRepository);

        assertThat(balance).isEqualTo(100.0);
    }

    @Test(expected = BankAccountNotFoundException.class)
    public void testSavingsFailureDueToUnknownBankAccount() {

        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.empty());

        SaveMoney savings = new SaveMoney(new AccountNumber("1-234-567-890"), 100);
        try {
            savingsAccountService.saveMoney(savings);
        }
        catch (Exception e) {
            verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
            verifyNoMoreInteractions(bankAccountRepository);
            throw e;
        }
        fail("The saveMoney method should have failed!");
    }

    @Test
    public void testSuccessfulMoneyWithdrawal() {

        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.of(new SavingsAccount(accountNumber)));

        SaveMoney savings = new SaveMoney(new AccountNumber("1-234-567-890"), 100);
        savingsAccountService.saveMoney(savings);

        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 1);
        double balance = savingsAccountService.withdrawMoney(withdraw);

        verify(bankAccountRepository, times(2)).findAccountByNumber(eq(accountNumber));
        verifyNoMoreInteractions(bankAccountRepository);

        assertThat(balance).isEqualTo(99.0);
    }

    @Test(expected = InsufficientFundsException.class)
    public void testWithdrawalFailureDueToInsufficientFunds() {

        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.of(new SavingsAccount(accountNumber)));

        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 100);
        try {
            savingsAccountService.withdrawMoney(withdraw);
        }
        catch (Exception e) {
            verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
            verifyNoMoreInteractions(bankAccountRepository);
            throw e;
        }
        fail("The withDrawMoney method should have failed due to insufficient funds!");
    }

    @Test(expected = BankAccountNotFoundException.class)
    public void testWithdrawalFailureDueToUnknownBankAccount() {

        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenReturn(Optional.empty());

        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 100);
        try {
            savingsAccountService.withdrawMoney(withdraw);
        }
        catch (Exception e) {
            verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
            verifyNoMoreInteractions(bankAccountRepository);
            throw e;
        }
        fail("The withDrawMoney method should have failed due to missing account!");

    }

    @Test(expected = SavingsAccountException.class)
    public void testWithdrawalFailureDueToOtherExceptions() {
        when(bankAccountRepository.findAccountByNumber(accountNumber))
                .thenThrow(new QueryTimeoutException("Query timed out!"));

        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 100);
        try {
            savingsAccountService.withdrawMoney(withdraw);
        }
        catch (Exception e) {
            verify(bankAccountRepository, times(1)).findAccountByNumber(eq(accountNumber));
            verifyNoMoreInteractions(bankAccountRepository);
            throw e;
        }
        fail("The withDrawMoney method should have failed due to query time out!");
    }

    @Test(expected = NullPointerException.class)
    public void testWithdrawalWithNullParameter() {
        WithdrawMoney withdraw = new WithdrawMoney(new AccountNumber("1-234-567-890"), 100);
        savingsAccountService.withdrawMoney(withdraw);
        fail("The withDrawMoney method should have failed due to null parameter!");
    }


    @Test(expected = NullPointerException.class)
    public void testSavingsWithNullParameter() {
        SaveMoney savings = new SaveMoney(new AccountNumber("1-234-567-890"), 100);
        savingsAccountService.saveMoney(savings);
        fail("The saveMoney method should have failed due to null parameter!");
    }

 }

The Controller Layer
^^^^^^^^^^^^^^^^^^^^

The **controller layer** represents a contract between our application and our clients, and we'd do well to test that those contracts are properly satisfied. The Spring Framework already provides very useful testing APIs that we can exploit for these purposes.

.. code-block:: java

 @RunWith(SpringRunner.class)
 @WebMvcTest(controllers = SavingsAccountController.class)
 public class SavingsAccountControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private final AccountNumber accountNumber = new AccountNumber("1-234-567-890");

    @MockBean
    private BankAccountService bankAccountService;

    @Autowired
    private MockMvc mvc;


    @Test
    public void testSavingMoney() throws Exception {

        SaveMoney savings = new SaveMoney(accountNumber, 100.0);

        given(bankAccountService.saveMoney(savings))
                .willReturn(savings.getAmount());

        RequestBuilder request = put("/accounts/save")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(getJsonString(savings));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber", equalTo("1-234-567-890")))
                .andExpect(jsonPath("$.balance", equalTo(100.0)))
                .andDo(print());

    }

    @Test
    public void testSavingsWithInvalidAmount() throws Exception {

        RequestBuilder request = put("/accounts/save")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{\"accountNumber\":\"1-234-567-890\", \"amount\": -100}");

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0]", equalTo("The amount must be > 0: -100.0")))
                .andDo(print());

    }

    @Test
    public void testSavingsWithInvalidAccountNumber() throws Exception {

        RequestBuilder request = put("/accounts/save")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{\"accountNumber\": null, \"amount\": 100}");

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0]", equalTo("The account number must not be null")))
                .andDo(print());

    }


    @Test
    public void testWithdrawingMoney() throws Exception {

        WithdrawMoney withdrawal = new WithdrawMoney(accountNumber, 100.0);

        given(bankAccountService.withdrawMoney(withdrawal))
                .willReturn(10.0);

        RequestBuilder request = put("/accounts/withdraw")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content(getJsonString(withdrawal));

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.accountNumber", equalTo("1-234-567-890")))
                .andExpect(jsonPath("$.balance", equalTo(10.0)))
                .andDo(print());

    }


    @Test
    public void testWithdrawalWithInvalidAmount() throws Exception {

        RequestBuilder request = put("/accounts/withdraw")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{\"accountNumber\":\"1-234-567-890\", \"amount\": -100}");

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0]", equalTo("The amount must be > 0: -100.0")))
                .andDo(print());

    }

    @Test
    public void testWithdrawalWithInvalidAccountNumber() throws Exception {

        RequestBuilder request = put("/accounts/withdraw")
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .content("{\"accountNumber\": null, \"amount\": 100}");

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(APPLICATION_JSON))
                .andExpect(jsonPath("$.messages[0]", equalTo("The account number must not be null")))
                .andDo(print());

    }


    private String getJsonString(Object source) throws Exception {
        return mapper.writeValueAsString(source);
    }

 }


Further Reading
---------------

* `Design By Contract`_
* `Null References: The Billion Dollar Mistake <https://www.infoq.com/presentations/Null-References-The-Billion-Dollar-Mistake-Tony-Hoare>`_
* `Java SE 8 Optional, a pragmatic approach <http://blog.joda.org/2015/08/java-se-8-optional-pragmatic-approach.html>`_
* `Objects Should Be Immutable`_
* `Data Transfer Object`_
* `Trouble with Checked Exceptions`_ 
* `The Exceptions Debate`_
* `Does Java Need Checked Exceptions?`_
* `Code Complete`_
* `Effective Java`_
* `Bean Validation`_
* `Input Validation Cheat Sheet`_

.. _Code Complete: https://www.amazon.com/Code-Complete-Practical-Handbook-Construction/dp/0735619670
.. _Effective Java: https://www.amazon.com/Effective-Java-3rd-Joshua-Bloch/dp/0134685997/
.. _Design By Contract: http://wiki.c2.com/?DesignByContract
.. _Objects Should Be Immutable: http://www.yegor256.com/2014/06/09/objects-should-be-immutable.html
.. _Data Transfer Object: https://martinfowler.com/eaaCatalog/dataTransferObject.html
.. _Bean Validation: http://beanvalidation.org
.. _Trouble with Checked Exceptions: http://www.artima.com/intv/handcuffs.html
.. _The Exceptions Debate: https://www.ibm.com/developerworks/library/j-jtp05254/index.html
.. _Does Java Need Checked Exceptions?: http://www.mindview.net/Etc/Discussions/CheckedExceptions
.. _Spring Retry: https://github.com/spring-projects/spring-retry
.. _TransientDataAccessException: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/dao/TransientDataAccessException.html
.. _QueryTimeoutException: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/dao/QueryTimeoutException.html
.. _Input Validation Cheat Sheet: https://www.owasp.org/index.php/Input_Validation_Cheat_Sheet
