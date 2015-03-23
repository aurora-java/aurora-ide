CLASSPATH=.
HOME=`dirname $PWD`
#echo $HOME

CLASSPATH="$CLASSPATH:""$HOME/WEB-INF/classes"
for jar in `ls $HOME/WEB-INF/lib/*` 
do
   CLASSPATH="$CLASSPATH:""$jar" 
done
#echo $CLASSPATH
java -cp $CLASSPATH aurora.application.admin.ServerAdmin 18080 $HOME &
