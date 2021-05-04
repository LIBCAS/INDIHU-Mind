import Button from "@material-ui/core/Button";
import Grid from "@material-ui/core/Grid";
import InputBase from "@material-ui/core/InputBase";
import InputLabel from "@material-ui/core/InputLabel";
import Typography from "@material-ui/core/Typography";
import Add from "@material-ui/icons/Add";
import classNames from "classnames";
import { Field, FieldProps, Form, FormikProps } from "formik";
import React, {
  useCallback,
  useContext,
  useEffect,
  useRef,
  useState,
} from "react";
import { RouteComponentProps, withRouter } from "react-router";
import { ButtonGrey } from "../../components/control/ButtonGrey";
import { Divider } from "../../components/divider/Divider";
import { Editor } from "../../components/editor/Editor";
import { Formik } from "../../components/form/Formik";
import { useStyles as useFormStyles } from "../../components/form/_styles";
import { Loader } from "../../components/loader/Loader";
import { MessageSnackbar } from "../../components/messages/MessageSnackbar";
import { Popover } from "../../components/portal/Popover";
import { GlobalContext } from "../../context/Context";
import { useStyles as useLayoutStyles } from "../../theme/styles/layoutStyles";
import { useStyles as useSpacingStyles } from "../../theme/styles/spacingStyles";
import { useStyles as useStylesText } from "../../theme/styles/textStyles";
import { theme } from "../../theme/theme";
import { AttributeProps } from "../../types/attribute";
import { CardTemplateProps } from "../../types/cardTemplate";
import { CategoryProps } from "../../types/category";
import { FileProps } from "../../types/file";
import { LabelProps } from "../../types/label";
import { RecordProps } from "../../types/record";
import { notEmpty, notLongerThan255 } from "../../utils/form/validate";
import { CardCreateAddAttribute } from "./CardCreateAddAttribute";
import { CardCreateAddCard } from "./CardCreateAddCard";
import { CardCreateAddCategory } from "./CardCreateAddCategory";
import { CardCreateAddFile } from "./CardCreateAddFile";
import { CardCreateAddLabel } from "./CardCreateAddLabel";
import { CardCreateAddRecord } from "./CardCreateAddRecord";
import { CardCreateAddTemplate } from "./CardCreateAddTemplate";
import { CardCreateAttribute } from "./CardCreateAttribute";
import { useStyles } from "./_cardCreateStyles";
import { getTemplateName, onSubmitCard } from "./_utils";

export interface InitValuesProps {
  id: string;
  name: string;
  note: string;
  categories: CategoryProps[];
  labels: LabelProps[];
  attributes: AttributeProps[];
  records: RecordProps[];
  documents?: FileProps[];
  cardContentId?: string;
  linkedCards?: { id: string; name: string; note: string }[];
}

let defaultInitialValues: InitValuesProps = {
  id: "",
  name: "",
  note: "",
  categories: [] as CategoryProps[],
  labels: [] as LabelProps[],
  records: [] as RecordProps[],
  attributes: [] as AttributeProps[],
  linkedCards: [],
  documents: [],
};

interface CardCreateFormProps {
  setOpen: Function;
  loadTemplates: Function;
  initValues: InitValuesProps;
  templates: CardTemplateProps[];
  edit?: boolean;
  afterEdit?: Function;
}

const CardCreateFormView: React.FC<
  CardCreateFormProps & RouteComponentProps
> = ({
  setOpen,
  templates,
  loadTemplates,
  initValues,
  history,
  edit,
  afterEdit,
}) => {
  const classes = useStyles();

  const classesText = useStylesText();

  const classesForm = useFormStyles();

  const classesSpacing = useSpacingStyles();

  const classesLayout = useLayoutStyles();

  const context: any = useContext(GlobalContext);

  const dispatch: Function = context.dispatch;

  const [loading, setLoading] = useState(false);

  const [error, setError] = useState(false);

  const [errorMessage, setErrorMessage] = useState<string | undefined>(
    undefined
  );

  const [initialValues, setInitialValues] = useState<InitValuesProps>(
    defaultInitialValues
  );

  const [popoverOpen, setPopoverOpen] = useState<boolean>(false);

  const [templateOpen, setTemplateOpen] = useState<boolean>(false);

  const onAddAttribute = useCallback(() => setPopoverOpen(true), []);

  const AddAttributeRef = useRef(null);

  const AddTemplateRef = useRef(null);

  useEffect(() => {
    if (initValues) {
      setInitialValues(initValues);
    } else {
      setInitialValues(defaultInitialValues);
    }
  }, [initValues]);

  useEffect(() => {
    if (edit && !initValues) {
      setLoading(true);
    } else {
      setLoading(false);
    }
  }, [edit, initValues]);
  return (
    <>
      <Loader loading={loading} />
      {error && (
        <MessageSnackbar
          setVisible={(closed: boolean) => {
            setError(closed);
            setErrorMessage(undefined);
          }}
          message={errorMessage}
        />
      )}
      <Formik
        initialValues={initialValues}
        enableReinitialize
        onSubmit={(values: any) => {
          if (loading) return;
          setLoading(true);
          onSubmitCard(
            values,
            setOpen,
            setError,
            setLoading,
            history,
            dispatch,
            setErrorMessage,
            afterEdit
          );
        }}
        render={(formikBag: FormikProps<any>) => (
          <Form>
            <div className={classes.wrapper}>
              <Field
                validate={(value: any) =>
                  notLongerThan255(value) || notEmpty(value)
                }
                name="name"
                render={({ field, form }: FieldProps<any>) => (
                  <>
                    <InputBase
                      className={classNames(
                        classesForm.title,
                        classes.cardTitle
                      )}
                      error={Boolean(form.touched.name && form.errors.name)}
                      autoFocus={false}
                      autoComplete="off"
                      {...field}
                      placeholder="Zadejte název"
                    />
                    <Typography
                      style={{
                        marginLeft: theme.spacing(3),
                        marginRight: theme.spacing(3),
                      }}
                      color="error"
                    >
                      {form.touched.name &&
                        form.errors.name &&
                        form.errors.name}
                    </Typography>
                    <Divider />
                  </>
                )}
              />
              <div className={classes.subWrapper}>
                <InputLabel
                  className={classNames(classesForm.label)}
                  htmlFor="note"
                >
                  Popis
                </InputLabel>
                <Field
                  name="note"
                  label="Popis"
                  onChange={(value: string) =>
                    formikBag.setFieldValue("note", value)
                  }
                  component={Editor}
                />

                <div
                  className={classNames(
                    classesLayout.flex,
                    classesLayout.flexWrap,
                    classesLayout.directionColumnMobile,
                    classesLayout.halfItemsWithSpaceBetween,
                    classesLayout.fullItemsMobile
                  )}
                >
                  <CardCreateAddCategory formikBag={formikBag} />
                  <CardCreateAddLabel formikBag={formikBag} />
                </div>

                <CardCreateAddRecord formikBag={formikBag} />

                <div className={classesSpacing.mt2} />
                <Typography className={classesText.subtitle}>
                  ATRIBUTY
                </Typography>
                <div className={classes.attributeItemsContainer}>
                  {formikBag.values.attributes.map((att: AttributeProps) => (
                    <div key={att.id} className={classes.attributeItemWrapper}>
                      <CardCreateAttribute
                        attribute={att}
                        formikBag={formikBag}
                      />
                    </div>
                  ))}
                </div>
                <div ref={AddAttributeRef} className={classes.addWrapper}>
                  <ButtonGrey
                    text="Přidat atribut"
                    onClick={onAddAttribute}
                    bold
                    Icon={<Add fontSize="small" />}
                  />
                  <Popover
                    open={popoverOpen}
                    setOpen={setPopoverOpen}
                    anchorEl={AddAttributeRef.current}
                    overflowVisible={true}
                    content={
                      <CardCreateAddAttribute
                        formikBagParent={formikBag}
                        setOpen={setPopoverOpen}
                      />
                    }
                  />
                </div>

                <CardCreateAddFile formikBag={formikBag} />

                <CardCreateAddCard formikBag={formikBag} />
              </div>

              <div className={classesSpacing.mb2} />
              <Divider />
              <div
                className={classNames(
                  classesSpacing.mt2,
                  classesSpacing.ml1,
                  classesSpacing.mr1
                )}
              >
                <Grid container justify="center" wrap="nowrap" spacing={1}>
                  <Grid item className={classes.templateButton}>
                    <Button
                      ref={AddTemplateRef}
                      className={classNames(classesSpacing.mb1)}
                      variant="outlined"
                      color="primary"
                      onClick={() => setTemplateOpen((prev) => !prev)}
                    >
                      Uložit jako šablonu
                    </Button>
                  </Grid>

                  <Popover
                    open={templateOpen}
                    setOpen={setTemplateOpen}
                    anchorEl={AddTemplateRef.current}
                    content={
                      formikBag.values.attributes.length === 0 ? (
                        <div
                          className={classNames(
                            classesText.textCenter,
                            classesSpacing.p1
                          )}
                        >
                          Pro uložení šablony je potřeba alespoň 1 atribut
                        </div>
                      ) : getTemplateName(templates, formikBag) ===
                        undefined ? (
                        <CardCreateAddTemplate
                          attributes={formikBag.values.attributes}
                          setTemplateOpen={setTemplateOpen}
                          loadTemplates={loadTemplates}
                        />
                      ) : (
                        <div
                          className={classNames(
                            classesText.textCenter,
                            classesSpacing.p1
                          )}
                        >
                          Šablona již existuje:{" "}
                          {getTemplateName(templates, formikBag)}
                        </div>
                      )
                    }
                  />
                  <Grid item>
                    <Button
                      variant="contained"
                      color="primary"
                      className={classesSpacing.mb1}
                      type="submit"
                    >
                      {edit ? "Změnit kartu" : "Vytvořit kartu"}
                    </Button>
                  </Grid>
                </Grid>
              </div>
            </div>
          </Form>
        )}
      />
    </>
  );
};

export const CardCreateForm = withRouter(CardCreateFormView);
