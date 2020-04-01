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