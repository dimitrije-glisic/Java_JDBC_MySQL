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