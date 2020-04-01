CREATE TYPE [MyDecimal]
	FROM DECIMAL(10,3) NULL
go

CREATE TYPE [Name]
	FROM VARCHAR(100) NULL
go

CREATE TABLE [Article]
( 
	[IdArticle]          integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Name]               [Name]  NOT NULL ,
	[Price]              integer  NOT NULL ,
	[Quantity]           integer  NOT NULL ,
	[IdShop]             integer  NULL 
)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([IdArticle] ASC)
go

CREATE TABLE [Buyer]
( 
	[IdBuyer]            integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Name]               [Name]  NOT NULL ,
	[Credit]             [MyDecimal]  NOT NULL ,
	[IdCity]             integer  NULL 
)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([IdBuyer] ASC)
go

CREATE TABLE [City]
( 
	[IdCity]             integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Name]               [Name]  NOT NULL 
)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdCity] ASC)
go

CREATE TABLE [CityToCity]
( 
	[IDCC]               integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[FirstCity]          integer  NOT NULL ,
	[SecondCity]         integer  NULL ,
	[Distance]           integer  NOT NULL 
)
go

ALTER TABLE [CityToCity]
	ADD CONSTRAINT [XPKCityToCity] PRIMARY KEY  CLUSTERED ([IDCC] ASC)
go

CREATE TABLE [Order]
( 
	[IdOrder]            integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Ammount]            [MyDecimal]  NOT NULL ,
	[IdBuyer]            integer  NULL ,
	[OrderState]         varchar(20)  NULL 
)
go

ALTER TABLE [Order]
	ADD CONSTRAINT [XPKOrder] PRIMARY KEY  CLUSTERED ([IdOrder] ASC)
go

CREATE TABLE [OrderArticle]
( 
	[IdOA]               integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[IdOrder]            integer  NOT NULL ,
	[Quantity]           integer  NULL ,
	[IdArticle]          integer  NULL 
)
go

ALTER TABLE [OrderArticle]
	ADD CONSTRAINT [XPKOrderArticle] PRIMARY KEY  CLUSTERED ([IdOA] ASC)
go

CREATE TABLE [Shop]
( 
	[IdShop]             integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Discount]           integer  NOT NULL ,
	[Name]               [Name]  NOT NULL ,
	[IdCity]             integer  NULL ,
	[Ammount]            char(18)  NULL 
)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([IdShop] ASC)
go

CREATE TABLE [System]
( 
	[IdSystem]           integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Systime]            datetime  NULL 
)
go

ALTER TABLE [System]
	ADD CONSTRAINT [XPKSystem] PRIMARY KEY  CLUSTERED ([IdSystem] ASC)
go

CREATE TABLE [Transaction]
( 
	[IdTransaction]      integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[IdBuyer]            integer  NOT NULL ,
	[IdOrder]            integer  NOT NULL ,
	[Ammount]            [MyDecimal] ,
	[ExecTime]           datetime  NULL 
)
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [XPKTransaction] PRIMARY KEY  CLUSTERED ([IdTransaction] ASC)
go

CREATE TABLE [TransactionShop]
( 
	[Identity]           integer  NOT NULL  IDENTITY ( 1,1 ) ,
	[Ammount]            [MyDecimal] ,
	[Status]             varchar(20)  NULL ,
	[IdTransaction]      integer  NOT NULL ,
	[IdShop]             integer  NULL 
)
go

ALTER TABLE [TransactionShop]
	ADD CONSTRAINT [XPKTransactionShop] PRIMARY KEY  CLUSTERED ([Identity] ASC,[IdTransaction] ASC)
go


ALTER TABLE [Article]
	ADD CONSTRAINT [R_26] FOREIGN KEY ([IdShop]) REFERENCES [Shop]([IdShop])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_27] FOREIGN KEY ([IdCity]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [CityToCity]
	ADD CONSTRAINT [R_24] FOREIGN KEY ([FirstCity]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [CityToCity]
	ADD CONSTRAINT [R_25] FOREIGN KEY ([SecondCity]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Order]
	ADD CONSTRAINT [R_28] FOREIGN KEY ([IdBuyer]) REFERENCES [Buyer]([IdBuyer])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go


ALTER TABLE [OrderArticle]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([IdOrder])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [OrderArticle]
	ADD CONSTRAINT [R_32] FOREIGN KEY ([IdArticle]) REFERENCES [Article]([IdArticle])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_31] FOREIGN KEY ([IdCity]) REFERENCES [City]([IdCity])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_12] FOREIGN KEY ([IdBuyer]) REFERENCES [Buyer]([IdBuyer])
		ON DELETE NO ACTION
		ON UPDATE CASCADE
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IdOrder]) REFERENCES [Order]([IdOrder])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [TransactionShop]
	ADD CONSTRAINT [R_36] FOREIGN KEY ([IdTransaction]) REFERENCES [Transaction]([IdTransaction])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [TransactionShop]
	ADD CONSTRAINT [R_40] FOREIGN KEY ([IdShop]) REFERENCES [Shop]([IdShop])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go



alter table [Transaction]
add StartTime datetime
go


--trigger

create trigger [dbo].[TR_TRANSFER_MONEY_TO_SHOPS]
ON [dbo].[Order]
FOR UPDATE
AS
BEGIN

DECLARE 
@IdOrder int,
@IdShop int,
@TransactionAmmount decimal(10,3),
@ShopAmmount decimal(10,3),
@OrderState varchar(20);


DECLARE cursor_ts cursor local
for select 
	IdShop,
	Ammount
FROM TransactionShop;


select @IdOrder = IdOrder
from inserted;


select @OrderState=OrderState from [Order]
where IdOrder = @IdOrder;
	


if(@OrderState = 'arrived')
begin
	
	open cursor_ts;
	
	fetch next from cursor_ts into
	@IdShop,@TransactionAmmount;
	
	while @@FETCH_STATUS = 0
	BEGIN	
	
	select @ShopAmmount = Ammount from Shop
	where IdShop = @IdShop;
		
	
	update Shop set Ammount = @ShopAmmount + @TransactionAmmount
	where IdShop = @IdShop;
	
	fetch next from cursor_ts into
	@IdShop,@TransactionAmmount;
	
	END;
	
	close cursor_ts;
	deallocate cursor_ts;

end;
	

end;
go


--procedure

create procedure [dbo].[spFinalPrice]
@orderId int,
@buyerId int
as
begin

declare @ammount decimal(10,3) = 0;
declare @temp decimal(10,3) = 0;

declare @IdArticle int,
		@Quantity int,
		@Price int,
		@IdShop int,
		@Discount decimal(10,3);


declare oa_cursor cursor local
for select IdArticle,Quantity
from OrderArticle;


open oa_cursor;



fetch next from oa_cursor into
@IdArticle, @Quantity;

while @@FETCH_STATUS = 0
begin

select @Price = Price
from Article where IdArticle = @IdArticle;

select @IdShop = IdShop
from Article where IdArticle = @IdArticle;

select @Discount = Discount
from Shop where IdShop= @IdShop;

set @temp = @Price * @Quantity * (100- @Discount)/100;

set @ammount += @temp;

fetch next from oa_cursor into
@IdArticle, @Quantity;

end;

close oa_cursor;
deallocate oa_cursor;

--additional discount

declare @Systime datetime

select @Systime = [Systime] from [System]
			where IdSystem = 1


declare @preAmmount decimal(10,3) = 0;

select @preAmmount = sum(Ammount) from [Transaction] where IdBuyer=@buyerId and (MONTH(@Systime) - MONTH(ExecTime) <= 30);

if(@preAmmount > 10000)
begin

set @ammount *= 0.98

end;

update [Order] set Ammount = @ammount
where IdOrder = @orderId;


end;
go